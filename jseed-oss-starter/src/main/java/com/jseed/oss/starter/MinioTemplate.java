package com.jseed.oss.starter;

import com.jseed.oss.starter.enums.PolicyType;
import com.jseed.oss.starter.model.BladeFile;
import com.jseed.oss.starter.model.OssFile;
import com.jseed.oss.starter.props.OssProperties;
import com.jseed.oss.starter.rule.OssRule;
import io.minio.*;
import io.minio.Http.Method;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteResult.Error;
import io.minio.messages.ListAllMyBucketsResult.Bucket;
import io.minio.messages.DeleteRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * MinIOTemplate
 *
 * @author cyrus
 */
@AllArgsConstructor
public class MinioTemplate implements OssTemplate {

    /**
     * MinIO client
     */
    private final MinioClient client;

    /**
     * Bucket naming rules
     */
    private final OssRule ossRule;

    /**
     * Configuration
     */
    private final OssProperties ossProperties;


    @Override
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (
                !client.bucketExists(
                        BucketExistsArgs.builder().bucket(getBucketName(bucketName)).build()
                )
        ) {
            client.makeBucket(
                    MakeBucketArgs.builder().bucket(getBucketName(bucketName)).build()
            );
            if (ossProperties.isPublicRead()) {
                client.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(getBucketName(bucketName))
                        .config(getPolicyType(getBucketName(bucketName), PolicyType.READ))
                        .build());
            }
        }
    }

    @SneakyThrows
    public Bucket getBucket() {
        return getBucket(ossProperties.getBucketName());
    }

    @SneakyThrows
    public Bucket getBucket(String bucketName) {
        Optional<Bucket> bucketOptional = client.listBuckets().stream().filter(bucket -> bucket.name().equals(getBucketName(bucketName))).findFirst();
        return bucketOptional.orElse(null);
    }

    @SneakyThrows
    public List<Bucket> listBuckets() {
        return client.listBuckets();
    }

    @Override
    @SneakyThrows
    public void removeBucket(String bucketName) {
        client.removeBucket(
                RemoveBucketArgs.builder().bucket(getBucketName(bucketName)).build()
        );
    }

    @Override
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        return client.bucketExists(
                BucketExistsArgs.builder().bucket(getBucketName(bucketName)).build()
        );
    }

    @Override
    @SneakyThrows
    public void copyFile(String bucketName, String fileName, String destBucketName) {
        copyFile(bucketName, fileName, destBucketName, fileName);
    }

    @Override
    @SneakyThrows
    public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
        client.copyObject(
                CopyObjectArgs.builder()
                        .source(SourceObject.builder().bucket(getBucketName(bucketName)).object(fileName).build())
                        .bucket(getBucketName(destBucketName))
                        .object(destFileName)
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public OssFile statFile(String fileName) {
        return statFile(ossProperties.getBucketName(), fileName);
    }

    @Override
    @SneakyThrows
    public OssFile statFile(String bucketName, String fileName) {
        StatObjectResponse stat = client.statObject(
                StatObjectArgs.builder().bucket(getBucketName(bucketName)).object(fileName).build()
        );
        OssFile ossFile = new OssFile();
        ossFile.setName(StringUtils.hasText(stat.object()) ? stat.object() : fileName);
        ossFile.setLink(fileLink(bucketName, ossFile.getName()));
        ossFile.setHash(stat.etag());
        ossFile.setLength(stat.size());
        ossFile.setPutTime(Date.from(stat.lastModified().toInstant()));
        ossFile.setContentType(stat.contentType());
        return ossFile;
    }

    @Override
    public String filePath(String fileName) {
        return filePath(ossProperties.getBucketName(), fileName);
    }

    @Override
    public String filePath(String bucketName, String fileName) {
        return getBucketName(bucketName).concat("/").concat(fileName);
    }

    @Override
    @SneakyThrows
    public String fileLink(String fileName) {
        return fileLink(ossProperties.getBucketName(), fileName);
    }

    @Override
    @SneakyThrows
    public String fileLink(String bucketName, String fileName) {
        return getEndpoint().concat("/").concat(filePath(bucketName, fileName));
    }

    @Override
    @SneakyThrows
    public BladeFile putFile(MultipartFile file) {
        return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
    }

    @Override
    @SneakyThrows
    public BladeFile putFile(String fileName, MultipartFile file) {
        return putFile(ossProperties.getBucketName(), fileName, file);
    }

    @Override
    @SneakyThrows
    public BladeFile putFile(String bucketName, String fileName, MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            String contentType = file.getContentType();
            if (!StringUtils.hasText(contentType)) {
                contentType = "application/octet-stream";
            }
            return putFile(bucketName, fileName, stream, contentType, true, file.getSize());
        }
    }

    /**
     * Uploads bytes under the supplied object name.
     */
    @SneakyThrows
    @Override
    public BladeFile putFile(String fileName, byte[] contentBytes) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(contentBytes)) {
            return putFile(fileName, stream, contentBytes.length);
        }
    }

    /**
     * Uploads a stream of the specified length under the supplied name. The caller closes the stream.
     */
    public BladeFile putFile(String fileName, InputStream stream, long size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0");
        }
        return putFile(ossProperties.getBucketName(), fileName, stream, "application/octet-stream", false, size);
    }

    @Override
    @SneakyThrows
    public BladeFile putFile(String fileName, InputStream stream) {
        return putFile(ossProperties.getBucketName(), fileName, stream);
    }

    @Override
    @SneakyThrows
    public BladeFile putFile(String fileName, InputStream stream, boolean isRename) {
        return putFile(ossProperties.getBucketName(), fileName, stream, "application/octet-stream", isRename);
    }

    @Override
    @SneakyThrows
    public BladeFile putFile(String bucketName, String fileName, InputStream stream) {
        return putFile(bucketName, fileName, stream, "application/octet-stream", true);
    }

    @SneakyThrows
    public BladeFile putFile(String bucketName, String fileName, InputStream stream, String contentType, boolean isRename) {
        return putFile(bucketName, fileName, stream, contentType, isRename, -1);
    }

    @SneakyThrows
    private BladeFile putFile(String bucketName, String fileName, InputStream stream, String contentType, boolean isRename, long size) {
        makeBucket(bucketName);
        String originalName = fileName;
        if (isRename) {
            fileName = getFileName(fileName);
        }
        client.putObject(
                PutObjectArgs.builder()
                        .bucket(getBucketName(bucketName))
                        .object(fileName)
                        .stream(stream, size >= 0 ? size : null, size >= 0 ? null : 10L * 1024 * 1024)
                        .contentType(contentType)
                        .build()
        );
        BladeFile file = new BladeFile();
        file.setOriginalName(originalName);
        file.setName(fileName);
        file.setDomain(getOssHost(bucketName));
        file.setLink(fileLink(bucketName, fileName));
        return file;
    }

    @Override
    @SneakyThrows
    public void removeFile(String fileName) {
        removeFile(ossProperties.getBucketName(), fileName);
    }

    @Override
    @SneakyThrows
    public void removeFile(String bucketName, String fileName) {
        client.removeObject(
                RemoveObjectArgs.builder().bucket(getBucketName(bucketName)).object(fileName).build()
        );
    }

    @Override
    @SneakyThrows
    public void removeFiles(List<String> fileNames) {
        removeFiles(ossProperties.getBucketName(), fileNames);
    }

    @Override
    @SneakyThrows
    public void removeFiles(String bucketName, List<String> fileNames) {
        Stream<DeleteRequest.Object> stream = fileNames.stream().map(DeleteRequest.Object::new);
        Iterable<Result<Error>> results = client.removeObjects(
                RemoveObjectsArgs.builder().bucket(getBucketName(bucketName)).objects(stream::iterator).build());
        List<String> failures = new ArrayList<>();
        for (Result<Error> result : results) {
            Error error = result.get();
            failures.add(error.objectName() + ": " + error.message());
        }
        if (!failures.isEmpty()) {
            throw new IllegalStateException("Failed to delete objects: " + String.join(", ", failures));
        }
    }

    @Override
    @SneakyThrows
    public File downloadFile(String fileName) {
        return downloadFile(ossProperties.getBucketName(), fileName);
    }

    @Override
    @SneakyThrows
    public File downloadFile(String bucketName, String fileName) {
        Path file = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir")), "jseed-oss-", ".tmp");
        boolean completed = false;
        try {
            try (InputStream stream = client.getObject(
                    GetObjectArgs.builder().bucket(getBucketName(bucketName)).object(fileName).build())) {
                Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
            }
            completed = true;
            return file.toFile();
        } finally {
            if (!completed) {
                Files.deleteIfExists(file);
            }
        }
    }

    @Override
    @SneakyThrows
    public byte[] downloadFileAsBytes(String fileName) {
        return downloadFileAsBytes(ossProperties.getBucketName(), fileName);
    }

    @Override
    @SneakyThrows
    public byte[] downloadFileAsBytes(String bucketName, String fileName) {
        try (InputStream inputStream = client.getObject(
                GetObjectArgs.builder()
                        .bucket(getBucketName(bucketName))
                        .object(fileName)
                        .build())) {

            return inputStream.readAllBytes();
        }
    }

    @Override
    @SneakyThrows
    public boolean isFileExists(String fileName) {
        try {
            client.statObject(
                    StatObjectArgs.builder()
                            .bucket(getBucketName((ossProperties.getBucketName())))
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code()) || "NoSuchBucket".equals(e.errorResponse().code())) {
                return false;
            }
            throw e;
        }
    }

    /**
     * Resolves the bucket name using the naming rule.
     *
     * @return String
     */
    private String getBucketName() {
        return getBucketName(ossProperties.getBucketName());
    }

    /**
     * Resolves the bucket name using the naming rule.
     *
     * @param bucketName bucket name
     * @return String
     */
    private String getBucketName(String bucketName) {
        return ossRule.bucketName(bucketName);
    }

    /**
     * Resolves the object name using the naming rule.
     *
     * @param originalFilename original file name
     * @return string
     */
    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }

    /**
     * Gets an external URL for the object.
     *
     * @param bucketName bucket name
     * @param fileName   object name
     * @param expires    expiration interval
     * @return url
     */
    @SneakyThrows
    public String getPresignedObjectUrl(String bucketName, String fileName, Integer expires) {
        return client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(getBucketName(bucketName))
                        .object(fileName)
                        .expiry(expires)
                        .build()
        );
    }

    /**
     * Gets the bucket policy.
     *
     * @param policyType policy type
     * @return String
     */
    public String getPolicyType(PolicyType policyType) {
        return getPolicyType(getBucketName(), policyType);
    }

    /**
     * Gets the bucket policy.
     *
     * @param bucketName bucket name
     * @param policyType policy type
     * @return String
     */
    public static String getPolicyType(String bucketName, PolicyType policyType) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("    \"Statement\": [\n");
        builder.append("        {\n");
        builder.append("            \"Action\": [\n");

        switch (policyType) {
            case WRITE:
                builder.append("                \"s3:GetBucketLocation\",\n");
                builder.append("                \"s3:ListBucketMultipartUploads\"\n");
                break;
            case READ_WRITE:
                builder.append("                \"s3:GetBucketLocation\",\n");
                builder.append("                \"s3:ListBucket\",\n");
                builder.append("                \"s3:ListBucketMultipartUploads\"\n");
                break;
            default:
                builder.append("                \"s3:GetBucketLocation\"\n");
                break;
        }

        builder.append("            ],\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("\"\n");
        builder.append("        },\n");
        if (PolicyType.READ.equals(policyType)) {
            builder.append("        {\n");
            builder.append("            \"Action\": [\n");
            builder.append("                \"s3:ListBucket\"\n");
            builder.append("            ],\n");
            builder.append("            \"Effect\": \"Deny\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::");
            builder.append(bucketName);
            builder.append("\"\n");
            builder.append("        },\n");

        }
        builder.append("        {\n");
        builder.append("            \"Action\": ");

        switch (policyType) {
            case WRITE:
                builder.append("[\n");
                builder.append("                \"s3:AbortMultipartUpload\",\n");
                builder.append("                \"s3:DeleteObject\",\n");
                builder.append("                \"s3:ListMultipartUploadParts\",\n");
                builder.append("                \"s3:PutObject\"\n");
                builder.append("            ],\n");
                break;
            case READ_WRITE:
                builder.append("[\n");
                builder.append("                \"s3:AbortMultipartUpload\",\n");
                builder.append("                \"s3:DeleteObject\",\n");
                builder.append("                \"s3:GetObject\",\n");
                builder.append("                \"s3:ListMultipartUploadParts\",\n");
                builder.append("                \"s3:PutObject\"\n");
                builder.append("            ],\n");
                break;
            default:
                builder.append("\"s3:GetObject\",\n");
                break;
        }

        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("/*\"\n");
        builder.append("        }\n");
        builder.append("    ],\n");
        builder.append("    \"Version\": \"2012-10-17\"\n");
        builder.append("}\n");
        return builder.toString();
    }

    /**
     * Gets the host URL.
     *
     * @param bucketName bucket name
     * @return String
     */
    public String getOssHost(String bucketName) {
        return getEndpoint() + "/" + getBucketName(bucketName);
    }

    /**
     * Gets the host URL.
     *
     * @return String
     */
    public String getOssHost() {
        return getOssHost(ossProperties.getBucketName());
    }

    /**
     * Gets the service endpoint.
     *
     * @return String
     */
    public String getEndpoint() {
        String transformEndpoint = ossProperties.getTransformEndpoint();
        return StringUtils.hasText(transformEndpoint) ? transformEndpoint : ossProperties.getEndpoint();
    }


}
