// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import com.jseed.datascope.starter.DataScope;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author cyrus
 */
@Mapper
public interface DemoMapper {
    @DataScope(table = "demo_item")
    @Select("SELECT id, department_id, label FROM demo_item ORDER BY id")
    List<Item> scopedItems();

    @DataScope(table = "demo_partition")
    @Select("SELECT label FROM demo_partition ORDER BY id")
    List<String> partitionItems();

    record Item(long id, long departmentId, String label) {}
}
