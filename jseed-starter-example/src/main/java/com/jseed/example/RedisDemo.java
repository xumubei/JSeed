// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import com.jseed.core.starter.model.R;
import com.jseed.redis.starter.lock.ILock;
import com.jseed.redis.starter.util.RedisUtil;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * @author cyrus
 */
@Profile("redis")
@RestController
@RequestMapping("/demo/redis")
public class RedisDemo {
    private final RedisUtil redis;
    private final ILock lock;

    public RedisDemo(RedisUtil redis, ILock lock) {
        this.redis = redis;
        this.lock = lock;
    }

    @PostMapping
    public R<String> cache(@RequestBody String value) {
        String result =
                lock.tryLock(
                        "example:lock",
                        () -> {
                            redis.setCacheObject("example:value", value, Duration.ofMinutes(1));
                            return redis.<String>getCacheObject("example:value");
                        });
        return R.ok(result);
    }
}
