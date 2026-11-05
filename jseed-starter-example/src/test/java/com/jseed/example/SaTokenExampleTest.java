// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author cyrus
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "sa-token.jwt-secret-key=demo-test-key-never-used-in-production")
@ActiveProfiles("satoken")
class SaTokenExampleTest {
    @LocalServerPort private int port;

    @org.springframework.beans.factory.annotation.Autowired private JsonMapper mapper;
    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    void loginReturnsATokenThatCanCallProtectedRoutes() throws Exception {
        assertThat(send("/me", false, null, null)).doesNotContain("example-user");
        var login = mapper.readTree(send("/login", true, null, null)).get("data");
        String name = login.get("tokenName").asString();
        String value = login.get("tokenValue").asString();
        assertThat(send("/me", false, name, value)).contains("example-user");
    }

    private String send(String path, boolean post, String header, String token) throws Exception {
        var request =
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/demo/auth" + path))
                        .timeout(java.time.Duration.ofSeconds(10));
        if (post) request.POST(HttpRequest.BodyPublishers.noBody());
        if (header != null) request.header(header, token);
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString()).body();
    }
}
