//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jseed.mybatis.starter.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Pagination request
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Data
public class ShallowPageParam implements Serializable {
    @Schema(description = "页大小")
    private Integer size;
    @Schema(description = "本次分页最后一个id")
    private Serializable endId;

    private static final Integer DEFAULT_SIZE = 10;

    public ShallowPageParam() {
        this.size = 10;
    }

    public ShallowPageParam(Integer size, Serializable endId) {
        this.setSize(size);
        this.setEndId(endId);
    }

    public void setSize(Integer size) {
        this.size = size < 1 ? DEFAULT_SIZE : size;
    }

    /**
     * Converts to a MyBatis page object.
     *
     * @param <T>
     * @return
     */
    public <T> Page<T> toPage() {
        return new Page<T>(1, size);
    }

}
