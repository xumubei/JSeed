// SPDX-License-Identifier: Apache-2.0
package com.jseed.tdengine.starter;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * JDBC resources use WebSocket transport; no relational transaction semantics are assumed.
 *
 * @author cyrus
 */
@AutoConfiguration
@EnableConfigurationProperties(TdengineProperties.class)
@ConditionalOnProperty(prefix = "jseed.tdengine", name = "enabled", havingValue = "true")
public class TdengineAutoConfiguration {
    @Bean(name = "tdengineDataSource", destroyMethod = "close", defaultCandidate = false)
    @ConditionalOnMissingBean(name = "tdengineDataSource")
    public HikariDataSource dataSource(TdengineProperties p) {
        if (!p.getUrl().startsWith("jdbc:TAOS-WS://"))
            throw new IllegalArgumentException("Use the TDengine WebSocket JDBC URL");
        if (p.getUsername() == null || p.getPassword() == null || p.getMaximumPoolSize() < 1)
            throw new IllegalArgumentException(
                    "TDengine credentials and a positive pool size are required");
        HikariDataSource source = new HikariDataSource();
        source.setPoolName("jseed-tdengine");
        source.setDriverClassName("com.taosdata.jdbc.ws.WebSocketDriver");
        source.setJdbcUrl(p.getUrl());
        source.setUsername(p.getUsername());
        source.setPassword(p.getPassword());
        source.setMaximumPoolSize(p.getMaximumPoolSize());
        source.setMinimumIdle(0);
        source.setInitializationFailTimeout(-1);
        return source;
    }

    @Bean(name = "tdengineJdbcTemplate", defaultCandidate = false)
    @ConditionalOnMissingBean(name = "tdengineJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("tdengineDataSource") javax.sql.DataSource source) {
        return new JdbcTemplate(source);
    }
}
