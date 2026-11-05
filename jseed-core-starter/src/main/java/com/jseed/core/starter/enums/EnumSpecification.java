package com.jseed.core.starter.enums;

import java.io.Serializable;

/**
 * Base enum interface
 *
 * @author cyrus
 * @date: 2024-07-10
 */
public interface EnumSpecification<T extends Serializable> {

    /**
     * Gets the identifier.
     *
     * @return
     */
    T getCode();

    /**
     * Gets the description.
     *
     * @return
     */
    String getDesc();

    /**
     * Compares values.
     *
     * @param code
     * @return
     */
    default boolean eq(T code) {
        return getCode().equals(code);
    }

}
