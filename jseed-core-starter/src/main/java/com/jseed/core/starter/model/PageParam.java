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
public class PageParam<T> implements Serializable {
    @Schema(description = "页大小")
    private Integer size;
    @Schema(description = "请求页码")
    private Integer current;
    @Schema(description = "查询条件实体")
    @Valid
    private T queryParam;

    private static final Integer DEFAULT_SIZE = 10;
    private static final Integer DEFAULT_CURRENT = 1;

    public PageParam() {
        this.current = 1;
        this.size = 10;
    }

    public PageParam(Integer current, Integer size) {
        this.setCurrent(current);
        this.setSize(size);
    }

    public void setSize(Integer size) {
        this.size = size < 1 ? DEFAULT_SIZE : size;
    }

    public void setCurrent(Integer current) {
        this.current = current <= 0 ? DEFAULT_CURRENT : current;
    }
}
