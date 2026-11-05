// SPDX-License-Identifier: Apache-2.0
package com.jseed.web.starter.log;

import com.jseed.web.starter.log.enums.BusinessType;

/**
 * Metadata only: no credentials, request payloads or response bodies.
 *
 * @author cyrus
 */
public record OperationLogEvent(
        String title,
        BusinessType businessType,
        String method,
        boolean success,
        long durationMillis,
        String errorType) {}
