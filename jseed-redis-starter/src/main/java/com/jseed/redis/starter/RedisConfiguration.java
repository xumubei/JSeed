package com.jseed.redis.starter;

import com.jseed.core.starter.utils.ObjectConverter;
import com.jseed.redis.starter.geo.*;
import com.jseed.redis.starter.lock.*;
import com.jseed.redis.starter.property.RedissonProperty;
import com.jseed.redis.starter.util.*;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cyrus
 */
@AutoConfiguration(
        afterName = "org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration")
@ConditionalOnProperty(prefix = "jseed.redis", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(RedissonProperty.class)
@EnableCaching
public class RedisConfiguration {
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(
            @Value("${jseed.redis.address:redis://localhost:6379}") String address,
            @Value("${spring.data.redis.password:}") String password,
            RedissonProperty properties) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec(ObjectConverter.getMapper()));
        config.setThreads(properties.getThreads());
        config.setNettyThreads(properties.getNettyThreads());
        var server = config.useSingleServer().setAddress(address);
        if (!password.isBlank()) server.setPassword(password);
        var tuning = properties.getSingleServerConfig();
        if (tuning != null) {
            if (tuning.getClientName() != null) server.setClientName(tuning.getClientName());
            if (tuning.getConnectionPoolSize() > 0)
                server.setConnectionPoolSize(tuning.getConnectionPoolSize());
            if (tuning.getConnectionMinimumIdleSize() > 0)
                server.setConnectionMinimumIdleSize(tuning.getConnectionMinimumIdleSize());
            if (tuning.getIdleConnectionTimeout() > 0)
                server.setIdleConnectionTimeout(tuning.getIdleConnectionTimeout());
            if (tuning.getTimeout() > 0) server.setTimeout(tuning.getTimeout());
            if (tuning.getSubscriptionConnectionPoolSize() > 0)
                server.setSubscriptionConnectionPoolSize(
                        tuning.getSubscriptionConnectionPoolSize());
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager jseedCacheManager(RedissonClient client, RedissonProperty properties) {
        Map<String, CacheConfig> caches = new HashMap<>();
        if (properties.getCacheGroup() != null) {
            for (var group : properties.getCacheGroup()) {
                CacheConfig cache = new CacheConfig(group.getTtl(), group.getMaxIdleTime());
                cache.setMaxSize(group.getMaxSize());
                caches.put(group.getGroupId(), cache);
            }
        }
        return new RedissonSpringCacheManager(
                client, caches, new JsonJacksonCodec(ObjectConverter.getMapper()));
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantEnvUtil tenantEnvUtil(RedissonProperty properties) {
        return new TenantEnvUtil(properties);
    }

    @Bean
    @ConditionalOnMissingBean(ILock.class)
    public ILock lock(RedissonClient client, TenantEnvUtil tenant) {
        return new RedisDistributedLock(client, tenant);
    }

    @Bean(name = "jseedRedisTemplate")
    @ConditionalOnMissingBean(name = "jseedRedisTemplate")
    public RedisTemplate<String, Object> jseedRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // Existing Jackson 2 values remain isolated from Boot's Jackson 3 HTTP mapper.
        var serializer =
                new Jackson2JsonRedisSerializer<>(ObjectConverter.getMapper(), Object.class);
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisUtil redisUtil(
            RedissonClient client,
            @org.springframework.beans.factory.annotation.Qualifier("jseedRedisTemplate")
                    RedisTemplate<String, Object> template,
            TenantEnvUtil tenant) {
        return new RedisUtil(client, template, tenant);
    }

    @Bean
    @ConditionalOnMissingBean
    public JSeedGeo jseedGeo(StringRedisTemplate template) {
        return new JSeedRedisGeoImpl(template);
    }
}
