// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import com.jseed.core.starter.model.R;
import com.jseed.core.starter.utils.ThreadLocalContext;
import com.jseed.crypto.starter.CryptoService;
import com.jseed.datascope.starter.DataScopeResolver;
import com.jseed.web.starter.log.annotation.Log;
import com.jseed.web.starter.serializer.Sensitive;
import com.jseed.web.starter.serializer.SensitiveStrategy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author cyrus
 */
@RestController
@RequestMapping("/demo")
public class DemoController {
    private final CryptoService crypto;
    private final DemoMapper mapper;

    public DemoController(CryptoService crypto, DemoMapper mapper) {
        this.crypto = crypto;
        this.mapper = mapper;
    }

    @GetMapping("/hello")
    @Log(title = "Example greeting")
    public R<Map<String, String>> hello() {
        return R.ok(Map.of("message", "Hello from JSeed"));
    }

    @GetMapping("/web")
    public R<Profile> profile() {
        return R.ok(
                new Profile(
                        Long.MAX_VALUE,
                        "13812345678",
                        "seed@example.com",
                        LocalDateTime.of(2026, 1, 2, 3, 4, 5)));
    }

    record Profile(
            long id,
            @Sensitive(strategy = SensitiveStrategy.PHONE) String phone,
            @Sensitive(strategy = SensitiveStrategy.EMAIL) String email,
            LocalDateTime createdAt) {}

    @GetMapping("/crypto")
    public R<Map<String, String>> crypto(@RequestParam(defaultValue = "hello") String text) {
        String key = crypto.generateKey();
        String ciphertext = crypto.encrypt(text, key);
        return R.ok(
                Map.of(
                        "ciphertext",
                        ciphertext,
                        "plaintext",
                        crypto.decrypt(ciphertext, key),
                        "sha256",
                        crypto.sha256(text)));
    }

    @GetMapping("/items")
    public R<List<DemoMapper.Item>> items() {
        return R.ok(mapper.scopedItems());
    }

    @GetMapping("/partition")
    public R<List<String>> partition() {
        ThreadLocalContext.put("demoPartition", "1");
        try {
            return R.ok(mapper.partitionItems());
        } finally {
            ThreadLocalContext.remove("demoPartition");
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ScopeConfiguration {
        @Bean
        DataScopeResolver demoScopeResolver() {
            // A real application resolves department IDs from its authenticated principal.
            return scope -> Set.of(10L);
        }
    }
}
