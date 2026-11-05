// SPDX-License-Identifier: Apache-2.0
package com.jseed.tdengine.starter;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cyrus
 */
class TdengineAutoConfigurationTest {
    @Test
    void configuresTdengineAlongsideThePrimaryRelationalDataSource() {
        var applicationDataSource = new DriverManagerDataSource();
        new ApplicationContextRunner()
                .withBean("dataSource", javax.sql.DataSource.class, () -> applicationDataSource)
                .withConfiguration(AutoConfigurations.of(TdengineAutoConfiguration.class))
                .withPropertyValues(
                        "jseed.tdengine.enabled=true",
                        "jseed.tdengine.url=jdbc:TAOS-WS://localhost:6041/demo",
                        "jseed.tdengine.username=demo",
                        "jseed.tdengine.password=integration-only")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(javax.sql.DataSource.class)).isSameAs(applicationDataSource);
                    var tdengine = context.getBean("tdengineDataSource", HikariDataSource.class);
                    assertThat(tdengine.getJdbcUrl()).isEqualTo("jdbc:TAOS-WS://localhost:6041/demo");
                    assertThat(context.getBean("tdengineJdbcTemplate", JdbcTemplate.class).getDataSource())
                            .isSameAs(tdengine);
                });
    }
}
