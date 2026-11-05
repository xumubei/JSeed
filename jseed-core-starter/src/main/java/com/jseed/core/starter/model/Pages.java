//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jseed.core.starter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import java.io.Serializable;

/**
 * Pagination request
 *
 * @author cyrus
 * @date: 2024-07-08
 */
@Data
public class Pages<T> implements Serializable {
    @Schema(description = "页大小")
    private Integer pageNum;
    @Schema(description = "请求页码")
    private Integer pageSize;
    @Schema(description = "查询条件实体")
    @Valid
    private T queryParam;

    private static final Integer DEFAULT_SIZE = 10;
    private static final Integer DEFAULT_CURRENT = 1;

    public Pages() {
        this.pageNum = 1;
        this.pageSize = 10;
    }

    public Pages(Integer pageNum, Integer pageSize) {
        this.setCurrent(pageNum);
        this.setSize(pageSize);
    }

    public void setSize(Integer pageSize) {
        this.pageSize = pageSize < 1 ? DEFAULT_SIZE : pageSize;
    }

    public void setCurrent(Integer pageNum) {
        this.pageNum = pageNum <= 0 ? DEFAULT_CURRENT : pageNum;
    }
}
