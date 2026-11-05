package com.jseed.core.starter.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author cyrus
 * @date: 2024-07-08
 */
@Slf4j
public class Helper {

    /**
     * Splits the list into batches.
     *
     * @param list original list
     * @param size batch size
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> subList(List<T> list, int size) {
        List<List<T>> lists = new ArrayList<>();
        if (Objects.isNull(list) || list.size() == 0) {
            return lists;
        }
        // Default to five items per batch.
        size = size == 0 ? 5 : size;
        // Return immediately if the list has fewer than size items.
        if (list.size() <= size) {
            lists.add(list);
            return lists;
        }
        // Round up.
        double totalPage = Math.ceil(list.size() / (double) size);
        for (int i = 0; i < totalPage; i++) {
            lists.add(list.subList(i * size, Math.min((i + 1) * size, list.size())));
        }
        return lists;
    }

    /**
     * Handles an exception without interrupting execution.
     *
     * @param log      logger
     * @param runnable operation to run
     */
    public static void notInterrupt(Logger log, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("SystemEx:", e);
        }
    }

    /**
     * Invokes the consumer if data is non-null.
     *
     * @param consumer consumer
     */
    public static <T> void apply(T data, Consumer<T> consumer) {
        if (filter(data)) {
            consumer.accept(data);
        }
    }

    /**
     * Converts non-null data and passes the result to the consumer.
     *
     * @param convert conversion function
     * @param <R>
     * @return
     */
    public static <T, R> void mapApply(T data, Function<T, R> convert, Consumer<R> consumer) {
        if (filter(data)) {
            consumer.accept(convert.apply(data));
        }
    }

    /**
     * Filters out empty items before passing a non-empty list to the consumer.
     *
     * @param list     input list
     * @param consumer consumer
     * @param <T>
     */
    public static <T> void listApply(Collection<T> list, Consumer<List<T>> consumer) {
        // Return false for an empty list.
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        // Filter the list once.
        filter(list);
        // Return false if the filtered list is empty.
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        consumer.accept(Lists.newArrayList(list));
    }

    /**
     * Filters out empty items, converts the remaining items, and passes them to the consumer.
     *
     * @param list     input list
     * @param convert  conversion function
     * @param consumer consumer
     * @param <T>
     * @param <R>
     */
    public static <T, R> void listMapApply(Collection<T> list, Function<T, R> convert, Consumer<List<R>> consumer) {
        // Return false for an empty list.
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        // Filter the list once.
        filter(list);
        // Return false if the filtered list is empty.
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        consumer.accept(list.stream().map(convert).collect(Collectors.toList()));
    }

    /**
     * Check strings for emptiness and other values for null.
     *
     * @param data
     * @return
     */
    public static <T> boolean filter(T data) {
        // Filter out empty values.
        if (Objects.isNull(data)) {
            return false;
        }
        // Check whether the string is empty.
        if (data instanceof String) {
            String str = (String) data;
            return StringUtils.isNotBlank(str) && !StringUtils.equals("null", str);
        }
        // Return true if the value is not a list.
        if (!ClassTypeUtil.isCollection(data.getClass())) {
            return true;
        }
        // Return false for an empty list.
        Collection<?> list = (Collection<?>) data;
        if (CollectionUtil.isEmpty(list)) {
            return false;
        }
        // Filter the list once.
        filter(list);
        // Return false if the filtered list is empty.
        if (CollectionUtil.isEmpty(list)) {
            return false;
        }
        return true;
    }

    /**
     * Remove null values and empty strings from the list.
     *
     * @param data
     * @param <T>
     */
    public static <T> void filter(Collection<T> data) {
        List<T> tmp = new ArrayList<>(data);
        tmp.forEach(it -> {
            if (!filter(it)) {
                data.remove(it);
            }
        });
    }

}
