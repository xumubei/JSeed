// SPDX-License-Identifier: Apache-2.0
package com.jseed.datascope.starter;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.jseed.mybatis.starter.interceptor.MybatisTableNameChangeInterceptor;
import com.jseed.mybatis.starter.property.SplitTableProperty;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author cyrus
 */
public class ScopePermissionHandler implements MultiDataPermissionHandler {
    private final DataScopeResolver resolver;
    private final SplitTableProperty split;
    private final Map<String, Optional<DataScope>> annotations = new ConcurrentHashMap<>();

    public ScopePermissionHandler(DataScopeResolver resolver, SplitTableProperty split) {
        this.resolver = resolver;
        this.split = split;
    }

    @Override
    public Expression getSqlSegment(Table table, Expression where, String statementId) {
        DataScope scope = scopeFor(statementId).orElse(null);
        if (scope == null || !matches(scope.table(), table.getName())) {
            return null;
        }
        if (!scope.column().matches("[A-Za-z_][A-Za-z0-9_]*"))
            throw new IllegalArgumentException("Invalid data scope column");
        Set<Long> ids = resolver.allowedIds(scope);
        String condition = "1 = 0";
        if (ids != null && !ids.isEmpty()) {
            if (ids.stream().anyMatch(Objects::isNull))
                throw new IllegalArgumentException("Null scope ID");
            String alias =
                    table.getAlias() == null
                            ? table.getFullyQualifiedName()
                            : table.getAlias().getName();
            condition =
                    alias
                            + "."
                            + scope.column()
                            + " IN ("
                            + ids.stream()
                                    .sorted()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(","))
                            + ")";
        }
        try {
            return CCJSqlParserUtil.parseCondExpression(condition);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot apply data scope", e);
        }
    }

    Optional<DataScope> scopeFor(String statementId) {
        return annotations.computeIfAbsent(statementId, this::findAnnotation);
    }

    private boolean matches(String logicalTable, String actualTable) {
        if (logicalTable.isEmpty() || logicalTable.equals(actualTable)) {
            return true;
        }
        return actualTable.startsWith(logicalTable + "_")
                && actualTable.equals(
                        MybatisTableNameChangeInterceptor.resolveTableName(split, logicalTable));
    }

    private Optional<DataScope> findAnnotation(String id) {
        int split = id.lastIndexOf('.');
        if (split < 0) return Optional.empty();
        try {
            Class<?> type = Class.forName(id.substring(0, split));
            String name = id.substring(split + 1);
            DataScope selected = null;
            for (Method method : type.getMethods())
                if (method.getName().equals(name) && method.isAnnotationPresent(DataScope.class)) {
                    if (selected != null && !selected.equals(method.getAnnotation(DataScope.class)))
                        throw new IllegalStateException("Ambiguous data scope on " + id);
                    selected = method.getAnnotation(DataScope.class);
                }
            return Optional.ofNullable(
                    selected != null ? selected : type.getAnnotation(DataScope.class));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
