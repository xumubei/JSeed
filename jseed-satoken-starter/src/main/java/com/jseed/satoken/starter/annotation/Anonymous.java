package com.jseed.satoken.starter.annotation;

import java.lang.annotation.*;

/**
 * Allows anonymous access to the annotated handler.
 *
 * @author cyrus
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {
}
