package com.school.edupoint.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.edupoint.model.StudentScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentScoreMapper extends BaseMapper<StudentScore> {
    // 按月份聚合总积分
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, SUM(total_point) AS total " +
            "FROM t_student_score GROUP BY month ORDER BY month ASC")
    List<Map<String, Object>> selectMonthlyPointTrend();
}
