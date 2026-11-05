package com.jseed.mybatis.starter.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.jseed.core.starter.utils.ThreadLocalContext;
import com.jseed.mybatis.starter.property.SplitTableProperty;

/**
 * Table identifiers are parsed by MyBatis-Plus, never replaced inside SQL literals.
 *
 * @author cyrus
 */
public class MybatisTableNameChangeInterceptor extends DynamicTableNameInnerInterceptor {
    public MybatisTableNameChangeInterceptor(SplitTableProperty properties) {
        setTableNameHandler((sql, table) -> resolveTableName(properties, table));
    }

    public static String resolveTableName(SplitTableProperty properties, String table) {
        if (properties.getTables() == null || !properties.getTables().contains(table)) {
            return table;
        }
        Object suffix = ThreadLocalContext.getValue(properties.getRule());
        if (suffix == null || !suffix.toString().matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException(
                    "Missing or invalid table suffix: " + properties.getRule());
        }
        return table + "_" + suffix;
    }
}
