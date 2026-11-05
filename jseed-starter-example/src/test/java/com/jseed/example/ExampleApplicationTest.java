// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.jseed.web.starter.log.OperationLogEvent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author cyrus
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"netty", "websocket"})
@org.springframework.context.annotation.Import(ExampleApplicationTest.LogCapture.class)
class ExampleApplicationTest {
    @LocalServerPort private int port;
    @Autowired private LogCapture events;

    private final HttpClient client =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    @Test
    void servesDocumentedEndpointsWithNoExternalServices() throws Exception {
        assertThat(get("/demo/hello")).contains("Hello from JSeed");
        assertThat(get("/demo/crypto?text=seed")).contains("\"plaintext\":\"seed\"");
        assertThat(get("/demo/items")).contains("visible").doesNotContain("filtered");
        assertThat(get("/demo/partition"))
                .contains("partition one")
                .doesNotContain("filtered partition row");
        assertThat(get("/v3/api-docs")).contains("JSeed examples", "/demo/crypto");
    }

    @Test
    void logsOperationMetadataWithoutLoggingRequestBodies() throws Exception {
        get("/demo/hello");
        assertThat(events.recorded)
                .anyMatch(event -> event.success() && event.title().equals("Example greeting"));
    }

    @Test
    void masksEachSensitiveFieldOnRepeatedResponsesAndPreservesLargeIds() throws Exception {
        String first = get("/demo/web");
        assertThat(first)
                .contains("\"id\":\"9223372036854775807\"", "138****5678", "2026-01-02 03:04:05")
                .doesNotContain("13812345678", "seed@example.com");
        assertThat(get("/demo/web")).isEqualTo(first);
    }

    @Test
    void echoesOverTcpAndWebSocket() throws Exception {
        var request =
                HttpRequest.newBuilder(uri("/demo/netty"))
                        .header("Content-Type", "text/plain")
                        .POST(HttpRequest.BodyPublishers.ofString("netty hello"))
                        .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("netty hello");

        CompletableFuture<String> message = new CompletableFuture<>();
        WebSocket socket =
                client.newWebSocketBuilder()
                        .buildAsync(
                                URI.create("ws://127.0.0.1:" + port + "/ws/jseed"),
                                new WebSocket.Listener() {
                                    @Override
                                    public java.util.concurrent.CompletionStage<?> onText(
                                            WebSocket ws, CharSequence data, boolean last) {
                                        message.complete(data.toString());
                                        return null;
                                    }
                                })
                        .get(5, TimeUnit.SECONDS);
        try {
            socket.sendText("websocket hello", true).get(5, TimeUnit.SECONDS);
            assertThat(message.get(5, TimeUnit.SECONDS)).isEqualTo("websocket hello");
        } finally {
            socket.abort();
        }
    }

    @org.springframework.boot.test.context.TestConfiguration(proxyBeanMethods = false)
    static class LogCapture {
        final java.util.concurrent.ConcurrentLinkedQueue<OperationLogEvent> recorded =
                new java.util.concurrent.ConcurrentLinkedQueue<>();

        @org.springframework.context.event.EventListener
        public void record(OperationLogEvent event) {
            recorded.add(event);
        }
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }

    private String get(String path) throws Exception {
        var request = HttpRequest.newBuilder(uri(path)).timeout(Duration.ofSeconds(10)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).as("%s: %s", path, response.body()).isEqualTo(200);
        return response.body();
    }
}
