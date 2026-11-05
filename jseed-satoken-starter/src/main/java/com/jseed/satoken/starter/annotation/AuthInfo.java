package com.jseed.satoken.starter.annotation;

import java.lang.annotation.*;

/**
 * Marks authenticated user information.
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthInfo {
    String value() default "";
}
