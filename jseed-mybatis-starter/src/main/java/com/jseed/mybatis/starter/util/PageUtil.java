package com.jseed.mybatis.starter.util;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jseed.core.starter.model.PageParam;
import com.jseed.core.starter.model.PageResponse;
import com.google.common.collect.Lists;
import com.jseed.core.starter.utils.ObjectConverter;

import java.util.List;

/**
 * Pagination utilities
 *
 * @author cyrus
 * @date: 2024-07-12
 */
public class PageUtil {

    /**
     * Converts the pagination request.
     *
     * @param pageParam
     * @return
     */
    public static <T> Page<T> transferBy(PageParam<?> pageParam) {
        return new Page<>(pageParam.getCurrent(), pageParam.getSize());
    }

    /**
     * Creates an empty PageResponse.
     *
     * @param page database page result
     * @return
     */
    public static <T, R> PageResponse<R> empty(Page<T> page) {
        PageResponse<R> response = new PageResponse<>();
        response.setPages(page.getPages());
        response.setSize(page.getSize());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        if (CollectionUtil.isEmpty(page.getRecords())) {
            response.setRecords(Lists.newArrayList());
        }
        return response;
    }

    /**
     * Converts a database page into the shared pagination response.
     *
     * @param page
     */
    public static <T> PageResponse<T> transferBy(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setPages(page.getPages());
        response.setSize(page.getSize());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setRecords(page.getRecords());
        if (CollectionUtil.isEmpty(page.getRecords())) {
            response.setRecords(Lists.newArrayList());
        }

        return response;
    }

    /**
     * Converts persistent objects in a page to view objects and returns a PageResponse.
     *
     * @param page       database page result
     * @param clazz      VO
     * @param <T>        database row type
     * @param <V>        converted view type
     * @return           page response containing the view objects
     */
    public static <T, V> PageResponse<V> transferToVO(Page<T> page, Class<V> clazz) {
        PageResponse<V> response = new PageResponse<>();
        response.setPages(page.getPages());
        response.setSize(page.getSize());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setRecords(ObjectConverter.convertList(page.getRecords(), clazz));
        return response;
    }

}
