// SPDX-License-Identifier: Apache-2.0
package com.jseed.core.starter.okhttp.utils;

import com.jseed.core.starter.okhttp.model.HttpMethod;
import com.jseed.core.starter.okhttp.model.HttpRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class RequestFactoryTest {
    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, names = {"GET", "DELETE", "WS"})
    void appendsEncodedParametersBeforeTheFragment(HttpMethod method) {
        HttpRequest params = new HttpRequest();
        params.put("q&sort", "中 文+&");
        params.put("empty", (Object) null);
        params.put("attachment", new File("ignored.txt"));
        params.putHeaders("X-Test", "present");
        String scheme = method == HttpMethod.WS ? "wss" : "https";
        var request = new RequestFactory(method, params, scheme + "://example.test/a?keep=a%2Bb#section")
                .initRequest();

        assertThat(request.method()).isEqualTo(method == HttpMethod.WS ? "GET" : method.name());
        assertThat(request.header("X-Test")).isEqualTo("present");
        assertThat(request.url().scheme()).isEqualTo("https");
        assertThat(request.url().fragment()).isEqualTo("section");
        assertThat(request.url().queryParameter("keep")).isEqualTo("a+b");
        assertThat(request.url().queryParameter("q&sort")).isEqualTo("中 文+&");
        assertThat(request.url().queryParameter("empty")).isEmpty();
        assertThat(request.url().queryParameterNames()).doesNotContain("attachment");
    }
}
