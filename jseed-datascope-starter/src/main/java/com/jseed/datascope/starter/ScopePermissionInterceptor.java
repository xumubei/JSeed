// SPDX-License-Identifier: Apache-2.0
package com.jseed.datascope.starter;

import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

final class ScopePermissionInterceptor extends DataPermissionInterceptor {
    private final ScopePermissionHandler handler;
    private final ThreadLocal<Boolean> matched = new ThreadLocal<>();

    ScopePermissionInterceptor(ScopePermissionHandler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    protected String processParser(Statement statement, int index, String sql, Object context) {
        String statementId = (String) context;
        if (handler.scopeFor(statementId).isEmpty()) {
            return super.processParser(statement, index, sql, context);
        }

        // A scope resolver can issue another mapper query on the same thread.
        Boolean previousMatch = matched.get();
        matched.set(false);
        try {
            String rewritten = super.processParser(statement, index, sql, context);
            if (!matched.get()) {
                throw new IllegalStateException("No table matched @DataScope on " + statementId);
            }
            return rewritten;
        } finally {
            if (previousMatch == null) {
                matched.remove();
            } else {
                matched.set(previousMatch);
            }
        }
    }

    @Override
    public Expression buildTableExpression(Table table, Expression where, String statementId) {
        Expression condition = super.buildTableExpression(table, where, statementId);
        if (condition != null && Boolean.FALSE.equals(matched.get())) {
            matched.set(true);
        }
        return condition;
    }
}
