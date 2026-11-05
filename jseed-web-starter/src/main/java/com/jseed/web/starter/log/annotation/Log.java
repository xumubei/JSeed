package com.jseed.web.starter.log.annotation;

import com.jseed.web.starter.log.enums.BusinessType;
import com.jseed.web.starter.log.enums.OperatorType;

import java.lang.annotation.*;

/**
 * Marks operations for audit logging.
 * 
 * @author cyrus
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * Module
     */
    public String title() default "";

    /**
     * Operation
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * Operator category
     */
    public OperatorType operatorType() default OperatorType.APP;

    /**
     * Whether to retain request parameters
     */
    public boolean isSaveRequestData() default true;

    /**
     * Whether to retain response data
     */
    public boolean isSaveResponseData() default true;

    /**
     * Request parameters to exclude
     */
    public String[] excludeParamNames() default {};
}
