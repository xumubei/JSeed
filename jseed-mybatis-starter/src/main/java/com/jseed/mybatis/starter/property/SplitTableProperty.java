package com.jseed.mybatis.starter.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * Table partitioning configuration
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Data
@ConfigurationProperties(prefix = "mybatis-plus.split")
public class SplitTableProperty {

    /**
     * Data sources excluded from table partitioning
     */
    private Set<String> unSplitDataSourceName;

    /**
     * Table partitioning rule
     * Thread-local context key containing the partition suffix
     *
     * @see com.jseed.core.starter.utils.ThreadLocalContext
     */
    private String rule;

    /**
     * Tables that use partition routing
     */
    private Set<String> tables;

}
