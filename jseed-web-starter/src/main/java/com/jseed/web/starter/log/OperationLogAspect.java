// SPDX-License-Identifier: Apache-2.0
package com.jseed.web.starter.log;

import com.jseed.web.starter.log.annotation.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author cyrus
 */
@Aspect
public class OperationLogAspect {
    private static final Logger LOG = LoggerFactory.getLogger(OperationLogAspect.class);
    private final ApplicationEventPublisher events;

    public OperationLogAspect(ApplicationEventPublisher events) {
        this.events = events;
    }

    @Around("@annotation(annotation)")
    public Object record(ProceedingJoinPoint point, Log annotation) throws Throwable {
        long start = System.nanoTime();
        Throwable failure = null;
        try {
            return point.proceed();
        } catch (Throwable error) {
            failure = error;
            throw error;
        } finally {
            OperationLogEvent event =
                    new OperationLogEvent(
                            annotation.title(),
                            annotation.businessType(),
                            point.getSignature().toLongString(),
                            failure == null,
                            (System.nanoTime() - start) / 1_000_000,
                            failure == null ? null : failure.getClass().getName());
            LOG.info(
                    "operation={} success={} durationMs={}",
                    event.title(),
                    event.success(),
                    event.durationMillis());
            try {
                events.publishEvent(event);
            } catch (RuntimeException error) {
                LOG.warn("Operation log listener failed", error);
            }
        }
    }
}
