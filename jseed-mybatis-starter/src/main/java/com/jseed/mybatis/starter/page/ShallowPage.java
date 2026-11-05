package com.jseed.mybatis.starter.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Additional pagination parameters
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShallowPage<T> extends com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> {

    private Serializable endId;

    public ShallowPage(Page<T> page) {
        super(page.getCurrent(), page.getSize(), page.getTotal());
        this.setPages(page.getPages());
        this.setRecords(page.getRecords());
    }

}
