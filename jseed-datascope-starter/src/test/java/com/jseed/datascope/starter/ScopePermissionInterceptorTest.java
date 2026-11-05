// SPDX-License-Identifier: Apache-2.0
package com.jseed.datascope.starter;

import com.jseed.mybatis.starter.property.SplitTableProperty;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class ScopePermissionInterceptorTest {
    private static final String SCOPED_STATEMENT = Mapper.class.getName() + ".scopedItems";
    private final ScopePermissionInterceptor interceptor = new ScopePermissionInterceptor(
            new ScopePermissionHandler(scope -> Set.of(10L), new SplitTableProperty()));

    @Test
    void rejectsProtectedQueryWhenNoTableMatches() {
        assertThatIllegalStateException()
                .isThrownBy(() -> interceptor.parserSingle("SELECT * FROM other_item", SCOPED_STATEMENT))
                .withMessageContaining("No table matched @DataScope")
                .withMessageContaining(SCOPED_STATEMENT);

        assertThat(interceptor.parserSingle("SELECT * FROM other_item", Mapper.class.getName() + ".allItems"))
                .isEqualTo("SELECT * FROM other_item");
    }

    @Test
    void filtersTheTargetTableInAJoin() {
        String sql = "SELECT i.id FROM demo_item i JOIN owner o ON i.owner_id = o.id";

        assertThat(interceptor.parserSingle(sql, SCOPED_STATEMENT))
                .contains("i.department_id IN (10)")
                .doesNotContain("o.department_id");
    }

    @Test
    void checksEachStatementInABatch() {
        String sql = "UPDATE demo_item SET label = 'updated'; DELETE FROM other_item";

        assertThatIllegalStateException()
                .isThrownBy(() -> interceptor.parserMulti(sql, SCOPED_STATEMENT))
                .withMessageContaining("No table matched @DataScope");
    }

    interface Mapper {
        @DataScope(table = "demo_item")
        void scopedItems();

        void allItems();
    }
}
