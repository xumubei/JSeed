package com.jseed.core.starter.enums;

import com.jseed.core.starter.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Error code contract
 *
 * @author cyrus
 * @date: 2024-07-10
 */
public interface ErrorEnumSpecification {

    /**
     * Error code
     *
     * @return
     */
    Integer getCode();

    /**
     * Error message
     *
     * @return
     */
    String getMsg();

    /**
     * Checks whether the map is empty.
     *
     * @param obj
     * @param params
     */
    default void isEmpty(Map<?, ?> obj, Object... params) {
        if (CollectionUtils.isEmpty(obj)) {
            throw new BusinessException(getCode(), getMsg(), params);
        }
    }

    /**
     * Checks whether the map is empty.
     *
     * @param obj     object to validate
     * @param message custom validation message
     * @param params  additional arguments
     */
    default void isEmpty(Map<?, ?> obj, String message, Object... params) {
        if (CollectionUtils.isEmpty(obj)) {
            throw new BusinessException(getCode(), getMsg(), params);
        }
    }

    /**
     * Checks whether the collection is empty.
     *
     * @param obj
     * @param params
     */
    default void isEmpty(Collection<?> obj, Object... params) {
        if (CollectionUtils.isEmpty(obj)) {
            throw new BusinessException(getCode(), getMsg(), params);
        }
    }

    /**
     * Checks whether the collection is empty.
     *
     * @param obj     object to validate
     * @param message custom validation message
     * @param params  additional arguments
     */
    default void isEmpty(Collection<?> obj, String message, Object... params) {
        if (CollectionUtils.isEmpty(obj)) {
            throw new BusinessException(getCode(), message, params);
        }
    }

    /**
     * Checks whether the object is null.
     *
     * @param obj    object to validate
     * @param params additional arguments
     */
    default void isNull(Object obj, Object... params) {
        if (Objects.isNull(obj)) {
            throw new BusinessException(getCode(), getMsg(), params);
        }
    }

    /**
     * Checks whether the object is null.
     *
     * @param obj     object to validate
     * @param message custom validation message
     * @param params  additional arguments
     */
    default void isNull(Object obj, String message, Object... params) {
        if (Objects.isNull(obj)) {
            throw new BusinessException(getCode(), message, params);
        }
    }

    /**
     * Checks whether the string is empty.
     *
     * @param str
     * @param params exception arguments
     */
    default void isBlank(String str, Object... params) {
        if (StringUtils.isBlank(str)) {
            throw new BusinessException(getCode(), getMsg(), params);
        }
    }

    /**
     * Throws an exception if the condition is true.
     *
     * @param condition condition to validate
     * @param message   custom validation message
     * @param params    exception arguments
     */
    default void isBlank(String condition, String message, Object... params) {
        if (StringUtils.isBlank(condition)) {
            throw new BusinessException(getCode(), message, params);
        }
    }

    /**
     * Throws an exception if the condition is true.
     *
     * @param condition condition to validate
     * @param message   custom validation message
     * @param params    exception arguments
     */
    default void check(boolean condition, String message, Object... params) {
        //Throws an error if the condition is true.
        if (condition) {
            throw new BusinessException(getCode(), message, params);
        }
    }

    /**
     * Throws an exception if the condition is true.
     *
     * @param condition condition to validate
     * @param params    exception arguments
     */
    default void check(boolean condition, Object... params) {
        //Throws an error if the condition is true.
        if (condition) {
            throw new BusinessException(getCode(), getMsg(), params);
        }
    }
}
