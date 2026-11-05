package com.jseed.web.starter.encryption.annotation;

import java.lang.annotation.*;

/**
 * Marks an endpoint whose response data field is encrypted.
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt {
}
