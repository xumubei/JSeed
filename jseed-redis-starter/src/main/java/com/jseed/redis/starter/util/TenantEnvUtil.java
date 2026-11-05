package com.jseed.redis.starter.util;


import com.jseed.redis.starter.property.RedissonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * Tenant context
 *
 * @author cyrus
 * @date: 2024-07-10
 */
@RequiredArgsConstructor
public class TenantEnvUtil implements InitializingBean {
    @NonNull
    private final RedissonProperty redissonProperty;
    /**
     * -- GETTER --
     *  Gets the Redis environment prefix.
     *
     * @return
     */
    @Getter
    private String envPrefix;

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isNotBlank(redissonProperty.getTenantId())) {
            envPrefix = String.format("env_%s_", redissonProperty.getTenantId());
        }

    }

    /**
     * Gets the tenant identifier.
     *
     * @return
     */
    public String getTenantId() {
        return redissonProperty.getTenantId();
    }

    /**
     * Adds the environment prefix to a Redis key.
     *
     * @param key
     * @return
     */
    public String envJoin(String key) {
        if (StringUtils.isNotBlank(envPrefix)) {
            return key.startsWith(envPrefix) ? key : envPrefix + key;
        }
        return key;
    }
}

