package com.jseed.core.starter.utils;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Deduplication utilities
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class DeduplicationPredicate {
    /**
     * Deduplicates items using a Predicate and a Function that extracts the comparison key.
     * Uses putIfAbsent on a ConcurrentHashMap to track keys atomically.
     *
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctBy(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = Maps.newConcurrentMap();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * Returns only duplicate items.
     *
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> repeatBy(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = Maps.newConcurrentMap();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) != null;
    }
}
