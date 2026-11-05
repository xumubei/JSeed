// SPDX-License-Identifier: Apache-2.0
package com.jseed.datascope.starter;

import java.lang.annotation.*;

/**
 * Resolves allowed numeric IDs through a business-owned resolver.
 *
 * @author cyrus
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataScope {
    String table() default "";

    String column() default "department_id";

    String key() default "departments";
}
