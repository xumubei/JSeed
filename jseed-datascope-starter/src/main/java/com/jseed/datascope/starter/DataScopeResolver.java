// SPDX-License-Identifier: Apache-2.0
package com.jseed.datascope.starter;

import java.util.Set;

/**
 * Return an empty set to deny access; no business identity model is assumed.
 *
 * @author cyrus
 */
@FunctionalInterface
public interface DataScopeResolver {
    Set<Long> allowedIds(DataScope scope);
}
