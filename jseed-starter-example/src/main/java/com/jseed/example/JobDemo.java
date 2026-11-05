// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import com.xxl.job.core.handler.annotation.XxlJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author cyrus
 */
@Profile("job")
@Component
public class JobDemo {
    private static final Logger LOG = LoggerFactory.getLogger(JobDemo.class);

    @XxlJob("jseedDemo")
    public void run() {
        LOG.info("JSeed demo job executed");
    }
}
