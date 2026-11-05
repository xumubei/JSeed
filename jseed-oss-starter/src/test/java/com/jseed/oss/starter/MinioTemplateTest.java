package com.jseed.oss.starter;

import com.jseed.oss.starter.model.BladeFile;
import com.jseed.oss.starter.model.OssFile;
import com.jseed.oss.starter.props.OssProperties;
import com.jseed.oss.starter.rule.OssRule;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteResult.Error;
import io.minio.messages.ErrorResponse;
import io.minio.messages.ListAllMyBucketsResult.Bucket;
import okhttp3.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author cyrus
 */
class MinioTemplateTest {
    private MinioClient client;
    private OssProperties properties;
    private MinioTemplate template;

    @BeforeEach
    void setUp() throws Exception {
        client = mock(MinioClient.class);
        properties = new OssProperties();
        properties.setBucketName("default-bucket");
        properties.setEndpoint("https://storage.example.com");
        OssRule rule = new OssRule() {
            public String bucketName(String name) { return name; }
            public String fileName(String name) { return "upload/" + name; }
        };
        template = new MinioTemplate(client, rule, properties);
        when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
    }

    @Test
    void downloadsContentIntoDistinctTemporaryFilesAndClosesResponses() throws Exception {
        byte[] data = "download content".getBytes(StandardCharsets.UTF_8);
        TrackedInputStream firstStream = new TrackedInputStream(data);
        TrackedInputStream secondStream = new TrackedInputStream(data);
        GetObjectResponse first = response(firstStream);
        GetObjectResponse second = response(secondStream);
        when(client.getObject(any(GetObjectArgs.class))).thenReturn(first, second);

        File one = template.downloadFile("other-bucket", "reports/report.txt");
        File two = template.downloadFile("other-bucket", "reports/report.txt");
        try {
            assertArrayEquals(data, Files.readAllBytes(one.toPath()));
            assertArrayEquals(data, Files.readAllBytes(two.toPath()));
            assertNotEquals(one, two);
            assertEquals(new File(System.getProperty("java.io.tmpdir")).getCanonicalFile(), one.getParentFile().getCanonicalFile());
            ArgumentCaptor<GetObjectArgs> args = ArgumentCaptor.forClass(GetObjectArgs.class);
            verify(client, times(2)).getObject(args.capture());
            assertEquals("other-bucket", args.getValue().bucket());
            assertEquals("reports/report.txt", args.getValue().object());
            assertTrue(firstStream.closed);
            assertTrue(secondStream.closed);
        } finally {
            Files.deleteIfExists(one.toPath());
            Files.deleteIfExists(two.toPath());
        }
    }

    @Test
    void downloadsBytesAndClosesTheResponse() throws Exception {
        byte[] data = {0, 1, -1};
        TrackedInputStream stream = new TrackedInputStream(data);
        when(client.getObject(any(GetObjectArgs.class))).thenReturn(response(stream));

        assertArrayEquals(data, template.downloadFileAsBytes("report.bin"));
        assertTrue(stream.closed);
    }

    @Test
    void removesPartialDownloadWhenReadingFails() throws Exception {
        Path temporaryDirectory = Files.createTempDirectory("jseed-download-test-");
        String original = System.getProperty("java.io.tmpdir");
        AtomicBoolean closed = new AtomicBoolean();
        InputStream broken = new InputStream() {
            public int read() throws IOException { throw new IOException("connection lost"); }
            public void close() { closed.set(true); }
        };
        GetObjectResponse response = response(broken);
        when(client.getObject(any(GetObjectArgs.class))).thenReturn(response);
        try {
            System.setProperty("java.io.tmpdir", temporaryDirectory.toString());
            assertThrows(IOException.class, () -> template.downloadFile("file.txt"));
            try (java.util.stream.Stream<Path> files = Files.list(temporaryDirectory)) {
                assertEquals(0, files.count());
            }
            assertTrue(closed.get());
        } finally {
            System.setProperty("java.io.tmpdir", original);
            try (java.util.stream.Stream<Path> files = Files.list(temporaryDirectory)) {
                for (Path path : (Iterable<Path>) files::iterator) Files.deleteIfExists(path);
            }
            Files.delete(temporaryDirectory);
        }
    }

    @Test
    void uploadsUnknownLengthWithoutUsingAvailableOrClosingCallerStream() throws Exception {
        byte[] data = "stream content".getBytes(StandardCharsets.UTF_8);
        TrackedInputStream stream = new TrackedInputStream(data);
        BladeFile file = template.putFile("report.txt", stream);

        ArgumentCaptor<PutObjectArgs> args = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(client).putObject(args.capture());
        assertNull(args.getValue().objectSize());
        assertTrue(args.getValue().partSize() >= 5 * 1024 * 1024);
        byte[] uploaded = new byte[data.length];
        assertEquals(data.length, args.getValue().stream().read(uploaded));
        assertArrayEquals(data, uploaded);
        assertEquals("upload/report.txt", file.getName());
        assertEquals(0, stream.availableCalls);
        assertFalse(stream.closed);
    }

    @Test
    void multipartUploadHonorsRequestedNameContentTypeSizeAndClosesStream() throws Exception {
        MultipartFile multipart = mock(MultipartFile.class);
        TrackedInputStream stream = new TrackedInputStream(new byte[]{1, 2, 3});
        when(multipart.getOriginalFilename()).thenReturn("original.txt");
        when(multipart.getInputStream()).thenReturn(stream);
        when(multipart.getSize()).thenReturn(3L);
        when(multipart.getContentType()).thenReturn("text/plain");

        BladeFile file = template.putFile("other-bucket", "requested.txt", multipart);

        ArgumentCaptor<PutObjectArgs> args = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(client).putObject(args.capture());
        assertEquals("other-bucket", args.getValue().bucket());
        assertEquals("upload/requested.txt", args.getValue().object());
        assertEquals("text/plain", args.getValue().contentType().toString());
        assertEquals(3, args.getValue().objectSize());
        assertEquals("requested.txt", file.getOriginalName());
        assertTrue(stream.closed);
    }

    @Test
    void multipartUploadClosesStreamOnStorageFailure() throws Exception {
        MultipartFile multipart = mock(MultipartFile.class);
        TrackedInputStream stream = new TrackedInputStream(new byte[]{1});
        when(multipart.getInputStream()).thenReturn(stream);
        when(multipart.getSize()).thenReturn(1L);
        MinioException failure = new MinioException("offline");
        when(client.putObject(any(PutObjectArgs.class))).thenThrow(failure);

        assertSame(failure, assertThrows(MinioException.class, () -> template.putFile("file.txt", multipart)));
        assertTrue(stream.closed);
    }

    @Test
    void byteArrayUploadPreservesExactNameAndLength() throws Exception {
        byte[] data = {1, 2, 3};
        BladeFile file = template.putFile("reports/report.txt", data);
        ArgumentCaptor<PutObjectArgs> args = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(client).putObject(args.capture());
        assertEquals(data.length, args.getValue().objectSize());
        assertEquals("reports/report.txt", args.getValue().object());
        assertEquals("reports/report.txt", file.getName());
        assertEquals("https://storage.example.com/default-bucket/reports/report.txt", file.getLink());
    }

    @Test
    void zeroLengthUploadIsSupported() throws Exception {
        template.putFile("empty.txt", new byte[0]);
        ArgumentCaptor<PutObjectArgs> args = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(client).putObject(args.capture());
        assertEquals(0, args.getValue().objectSize());
    }

    @Test
    void knownLengthUploadRejectsNegativeSizeBeforeAccessingStorage() {
        assertThrows(IllegalArgumentException.class,
                () -> template.putFile("file.txt", new ByteArrayInputStream(new byte[0]), -1L));
        verifyNoInteractions(client);
    }

    @Test
    void defaultBucketAppliesCustomNamingRuleOnce() throws Exception {
        OssRule rule = mock(OssRule.class);
        when(rule.bucketName(anyString())).thenAnswer(call -> "tenant-" + call.getArgument(0));
        template = new MinioTemplate(client, rule, properties);
        Bucket bucket = mock(Bucket.class);
        when(bucket.name()).thenReturn("tenant-default-bucket");
        when(client.listBuckets()).thenReturn(Collections.singletonList(bucket));
        assertSame(bucket, template.getBucket());
    }

    @Test
    void batchDeletionConsumesLazyResults() {
        AtomicBoolean consumed = new AtomicBoolean();
        when(client.removeObjects(any(RemoveObjectsArgs.class))).thenReturn(() -> {
            consumed.set(true);
            return Collections.<Result<Error>>emptyList().iterator();
        });
        template.removeFiles("other-bucket", Arrays.asList("a.txt", "b.txt"));
        assertTrue(consumed.get());
    }

    @Test
    void batchDeletionReportsObjectErrors() {
        Error error = mock(Error.class);
        when(error.objectName()).thenReturn("locked.txt");
        when(error.message()).thenReturn("Access Denied");
        when(client.removeObjects(any(RemoveObjectsArgs.class))).thenReturn(Collections.singletonList(new Result<>(error)));
        IllegalStateException failure = assertThrows(IllegalStateException.class, () -> template.removeFiles(Collections.singletonList("locked.txt")));
        assertTrue(failure.getMessage().contains("locked.txt"));
    }

    @Test
    void statUsesRequestedBucketContentHashAndAbsoluteTimestamp() throws Exception {
        StatObjectResponse stat = mock(StatObjectResponse.class);
        ZonedDateTime modified = ZonedDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(8));
        when(stat.object()).thenReturn("report.txt");
        when(stat.etag()).thenReturn("content-etag");
        when(stat.lastModified()).thenReturn(modified);
        when(client.statObject(any(StatObjectArgs.class))).thenReturn(stat);
        TimeZone original = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            OssFile file = template.statFile("other-bucket", "report.txt");
            assertEquals("https://storage.example.com/other-bucket/report.txt", file.getLink());
            assertEquals("content-etag", file.getHash());
            assertEquals(Date.from(modified.toInstant()), file.getPutTime());
        } finally {
            TimeZone.setDefault(original);
        }
    }

    @Test
    void existenceReturnsFalseForMissingObjectsAndPropagatesAccessErrors() throws Exception {
        ErrorResponseException missing = error("NoSuchKey");
        ErrorResponseException missingBucket = error("NoSuchBucket");
        ErrorResponseException denied = error("AccessDenied");
        when(client.statObject(any(StatObjectArgs.class)))
                .thenThrow(missing, missingBucket, denied, new MinioException("offline"))
                .thenReturn(mock(StatObjectResponse.class));
        assertFalse(template.isFileExists("missing.txt"));
        assertFalse(template.isFileExists("missing-bucket.txt"));
        assertSame(denied, assertThrows(ErrorResponseException.class, () -> template.isFileExists("private.txt")));
        assertThrows(MinioException.class, () -> template.isFileExists("offline.txt"));
        assertTrue(template.isFileExists("existing.txt"));
    }

    private GetObjectResponse response(InputStream stream) {
        return new GetObjectResponse(new Headers.Builder().build(), "other-bucket", "", "file.txt", stream);
    }

    private static class TrackedInputStream extends ByteArrayInputStream {
        private boolean closed;
        private int availableCalls;

        TrackedInputStream(byte[] data) { super(data); }

        @Override
        public int available() {
            availableCalls++;
            return 0;
        }

        @Override
        public void close() { closed = true; }
    }

    private ErrorResponseException error(String code) {
        ErrorResponse error = mock(ErrorResponse.class);
        when(error.code()).thenReturn(code);
        when(error.message()).thenReturn(code);
        return new ErrorResponseException(error, null, "");
    }
}
