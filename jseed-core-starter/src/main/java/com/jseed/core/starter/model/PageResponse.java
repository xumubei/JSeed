package com.jseed.core.starter.model;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Paginated response
 *
 * @author cyrus
 * @date: 2024-07-08
 */
@Data
public class PageResponse<T> {
    @Schema(description = "总记录条数", required = true)
    private long total;
    @Schema(description = "当前页码", required = true)
    private long current;
    @Schema(description = "总页数", required = true)
    private long pages;
    @Schema(description = "当前页大小", required = true)
    private long size;
    @Schema(description = "数据结果集", required = true)
    private List<T> records;

    /**
     * Creates an empty response.
     *
     * @param pageParam pagination request
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> of(PageParam<?> pageParam) {
        PageResponse<T> r = new PageResponse<>();
        r.setPages(0);
        r.setTotal(0);
        r.setSize(pageParam.getSize());
        r.setCurrent(pageParam.getCurrent());
        r.setRecords(Lists.newArrayList());
        return r;
    }

    /**
     * Creates an empty response.
     *
     * @param current current page number
     * @param size    page size
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> of(Integer current, Integer size) {
        PageResponse<T> r = new PageResponse<>();
        r.setPages(0);
        r.setTotal(0);
        r.setSize(size);
        r.setCurrent(current);
        r.setRecords(Lists.newArrayList());
        return r;
    }

    @SuppressWarnings("unchecked")
    public <R> PageResponse<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getRecords().stream().map(mapper).collect(toList());
        PageResponse<R> response = (PageResponse<R>) this;
        response.setRecords(collect);
        return response;
    }
    public <R> PageResponse<R> flatConvert(Function<? super T, ? extends Stream<? extends R>> mapper) {
        List<R> collect = this.getRecords().stream().flatMap(mapper).collect(toList());
        PageResponse<R> response = (PageResponse<R>) this;
        response.setRecords(collect);
        return response;
    }
}
