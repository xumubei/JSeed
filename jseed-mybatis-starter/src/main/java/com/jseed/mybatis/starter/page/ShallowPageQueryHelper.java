package com.jseed.mybatis.starter.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Automatically fetches pages using a cursor.
 *
 * @author cyrus
 * @date: 2024-07-12
 */
public class ShallowPageQueryHelper<T> {

    /**
     * Whether the query has finished
     */
    private boolean isLast;

    /**
     * Page size
     */
    private int size = 10;

    /**
     * Last ID in the current page
     */
    private Serializable endId;

    /**
     * Page query function
     */
    private Function<ShallowPageParam, ShallowPage<T>> function;

    private ShallowPageQueryHelper() {
    }

    public static <T> ShallowPageQueryHelper<T> getInstance() {
        return new ShallowPageQueryHelper<>();
    }

    public static <T> ShallowPageQueryHelper<T> getInstance(int size) {
        ShallowPageQueryHelper<T> shallowPageQueryHelper = new ShallowPageQueryHelper<>();
        shallowPageQueryHelper.size = size;
        return shallowPageQueryHelper;
    }

    /**
     * Checks whether another page is available.
     *
     * @return true if another page is available, false otherwise
     */
    private boolean hasNext() {
        return !isLast;
    }

    /**
     * Sets the page query function.
     *
     * @param function
     * @return
     */
    public ShallowPageQueryHelper<T> function(Function<ShallowPageParam, ShallowPage<T>> function) {
        this.function = function;
        return this;
    }

    /**
     * Collects all results produced by the query function.<br/>
     * Collecting all rows at once can use significant memory. Use {@link #consumer(Consumer)} to process one batch at a time.
     *
     * @return
     */
    public List<T> getTotalRecords() {
        List<T> totalRecords = new ArrayList<>();
        //Query synchronously when asynchronous queries are disabled or the page count is below the asynchronous threshold.
        while (this.hasNext()) {
            totalRecords.addAll(result(function));
        }
        return totalRecords;
    }

    /**
     * Processes one page at a time without collecting all results.
     *
     * @param consumer
     */
    public void consumer(Consumer<List<T>> consumer) {
        while (this.hasNext()) {
            consumer.accept(result(function));
        }
    }


    /**
     * Fetches the next page.
     *
     * @param function page query function
     * @return
     */
    private List<T> result(Function<ShallowPageParam, ShallowPage<T>> function) {
        ShallowPage<T> apply = function.apply(new ShallowPageParam(this.size, endId));
        this.endId = apply.getEndId();
        if (!apply.hasNext()) {
            this.isLast = true;
        }
        return Objects.isNull(apply.getRecords()) ? new ArrayList<>() : apply.getRecords();
    }
}
