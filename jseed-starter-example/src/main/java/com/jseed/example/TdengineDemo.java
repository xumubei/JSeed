// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author cyrus
 */
@Profile("tdengine")
@RestController
@RequestMapping("/demo/tdengine")
public class TdengineDemo {
    private final JdbcTemplate jdbc;

    public TdengineDemo(@Qualifier("tdengineJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping
    public List<Map<String, Object>> write() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS jseed_demo (ts TIMESTAMP, value DOUBLE)");
        long now = System.currentTimeMillis();
        jdbc.batchUpdate(
                "INSERT INTO jseed_demo VALUES (?, ?)",
                List.of(
                        new Object[] {new Timestamp(now), 1.0},
                        new Object[] {new Timestamp(now + 1), 2.0}));
        return jdbc.queryForList("SELECT * FROM jseed_demo ORDER BY ts DESC LIMIT 2");
    }
}
