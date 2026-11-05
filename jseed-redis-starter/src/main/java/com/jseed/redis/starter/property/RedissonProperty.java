package com.jseed.redis.starter.property;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Redisson settings
 *
 * @author cyrus
 */
@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperty {

    /**
     * Tenant ID
     */
    private String tenantId;

    /**
     * Worker thread count, defaulting to twice the CPU core count.
     */
    private int threads;

    /**
     * Netty thread count, defaulting to twice the CPU core count.
     */
    private int nettyThreads;

    /**
     * Standalone server settings
     */
    private SingleServerConfig singleServerConfig;

    /**
     * Cluster server settings
     */
    private ClusterServersConfig clusterServersConfig;

    /**
     * Cache groups
     */
    private List<CacheGroup> cacheGroup;

    @Data
    @NoArgsConstructor
    public static class SingleServerConfig {

        /**
         * Client name
         */
        private String clientName;

        /**
         * Minimum number of idle connections
         */
        private int connectionMinimumIdleSize;

        /**
         * Connection pool size
         */
        private int connectionPoolSize;

        /**
         * Idle connection timeout in milliseconds
         */
        private int idleConnectionTimeout;

        /**
         * Command timeout in milliseconds
         */
        private int timeout;

        /**
         * Publish and subscribe connection pool size
         */
        private int subscriptionConnectionPoolSize;

    }

    @Data
    @NoArgsConstructor
    public static class ClusterServersConfig {

        /**
         * Client name
         */
        private String clientName;

        /**
         * Minimum idle primary connections
         */
        private int masterConnectionMinimumIdleSize;

        /**
         * Primary connection pool size
         */
        private int masterConnectionPoolSize;

        /**
         * Minimum idle replica connections
         */
        private int slaveConnectionMinimumIdleSize;

        /**
         * Replica connection pool size
         */
        private int slaveConnectionPoolSize;

        /**
         * Idle connection timeout in milliseconds
         */
        private int idleConnectionTimeout;

        /**
         * Command timeout in milliseconds
         */
        private int timeout;

        /**
         * Publish and subscribe connection pool size
         */
        private int subscriptionConnectionPoolSize;

        /**
         * Read mode
         */
        private ReadMode readMode;

        /**
         * Subscription mode
         */
        private SubscriptionMode subscriptionMode;

    }

    @Data
    @NoArgsConstructor
    public static class CacheGroup {

        /**
         * Group ID
         */
        private String groupId;

        /**
         * Group TTL
         */
        private long ttl;

        /**
         * Maximum idle time for the group
         */
        private long maxIdleTime;

        /**
         * Maximum group size
         */
        private int maxSize;

    }

}
