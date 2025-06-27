package com.school.edupoint.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.edupoint.model.StudentActivityHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentActivityHistoryMapper extends BaseMapper<StudentActivityHistory> {


    void updatePosterByActivityName(@Param("activityName") String activityName, @Param("poster") String poster);

    @Select("SELECT a.poster FROM t_activity a WHERE a.title = #{activityName}")
    String getPosterByActivityName(@Param("activityName") String activityName);
    // 获取活动状态统计
    @Select("SELECT status, COUNT(*) AS count FROM t_student_activity_history GROUP BY status")
    List<Map<String, Object>> selectStatusStatistics();
}
