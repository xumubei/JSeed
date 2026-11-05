// SPDX-License-Identifier: Apache-2.0
package com.jseed.job.starter;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cyrus
 */
class JobAutoConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JobAutoConfiguration.class));

    @Test
    void doesNotStartAnExecutorUnlessEnabled() {
        runner.run(context -> assertThat(context).doesNotHaveBean(XxlJobSpringExecutor.class));
    }

    @Test
    void rejectsEnabledExecutorWithoutAdminAndToken() {
        runner.withPropertyValues("jseed.job.enabled=true").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure())
                    .hasRootCauseMessage("jseed.job.admin-addresses and access-token are required");
        });
    }
}
