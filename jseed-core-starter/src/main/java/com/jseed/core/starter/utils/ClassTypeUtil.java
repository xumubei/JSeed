package com.jseed.core.starter.utils;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Field type inspection utilities
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class ClassTypeUtil {

    /**
     * Checks whether the type is primitive or a boxed primitive.
     *
     * @param type field type
     * @return
     */
    public static boolean isBasicType(Class<?> type) {
        return type.isPrimitive() ||
                Lists.<Class<?>>newArrayList(
                        String.class,
                        Short.class,
                        Integer.class,
                        Long.class,
                        Float.class,
                        Double.class,
                        Byte.class,
                        Void.class,
                        Character.class,
                        Boolean.class,
                        BigDecimal.class,
                        BigInteger.class,
                        Number.class
                ).contains(type);

    }

    /**
     * Checks whether the type is a regular object.
     * Excludes primitives, boxed primitives, strings, and collections.
     *
     * @param type type
     * @return
     */
    public static boolean isEntity(Class<?> type) {
        return !isBasicType(type) && !isCollection(type) && !isMap(type) && !String.class.equals(type) && !Object.class.equals(type);
    }

    public static boolean isCollection(Class<?> type) {
        List<Class<?>> classes = Arrays.asList(type.getInterfaces());
        // Also accepts the collection base type itself.
        return Lists.<Class<?>>newArrayList(Collection.class, List.class, Set.class).contains(type)
                // Accepts types that implement the List interface.
                || classes.contains(List.class)
                // Accepts types that implement the Set interface.
                || classes.contains(Set.class)
                // Accepts types that implement the Collection interface.
                || classes.contains(Collection.class);
    }

    public static boolean isMap(Class<?> type) {
        return Map.class.equals(type) || Arrays.asList(type.getInterfaces()).contains(Map.class);
    }

    /**
     * Checks whether the field belongs to the system.
     *
     * @param field field to inspect
     * @return
     */
    public static boolean isSystemType(Field field) {
        return Modifier.isStatic(field.getModifiers())
                || Modifier.isFinal(field.getModifiers())
                || Modifier.isTransient(field.getModifiers())
                || Modifier.isAbstract(field.getModifiers())
                || Modifier.isInterface(field.getModifiers())
                || Modifier.isNative(field.getModifiers());
    }
}
