package com.school.edupoint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseSyncService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void syncActivityPoster() {
        // 1. 添加poster字段（如果不存在）
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE t_student_activity_history " +
                            "ADD COLUMN IF NOT EXISTS poster VARCHAR(100) COMMENT '海报' AFTER activity_name"
            );
        } catch (Exception e) {
            // 如果字段已存在，忽略错误
        }

        // 2. 更新现有记录的poster值
        jdbcTemplate.execute(
                "UPDATE t_student_activity_history sah " +
                        "INNER JOIN t_activity a ON sah.activity_name = a.title " +
                        "SET sah.poster = a.poster"
        );

        // 3. 删除已存在的触发器（如果存在）
        try {
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS after_activity_update");
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS after_activity_insert");
        } catch (Exception e) {
            // 忽略错误
        }

        // 4. 创建更新触发器
        jdbcTemplate.execute(
                "CREATE TRIGGER after_activity_update " +
                        "AFTER UPDATE ON t_activity " +
                        "FOR EACH ROW " +
                        "BEGIN " +
                        "    IF NEW.poster != OLD.poster THEN " +
                        "        UPDATE t_student_activity_history " +
                        "        SET poster = NEW.poster " +
                        "        WHERE activity_name = NEW.title; " +
                        "    END IF; " +
                        "END"
        );

        // 5. 创建插入触发器
        jdbcTemplate.execute(
                "CREATE TRIGGER after_activity_insert " +
                        "AFTER INSERT ON t_activity " +
                        "FOR EACH ROW " +
                        "BEGIN " +
                        "    UPDATE t_student_activity_history " +
                        "    SET poster = NEW.poster " +
                        "    WHERE activity_name = NEW.title; " +
                        "END"
        );
    }
}