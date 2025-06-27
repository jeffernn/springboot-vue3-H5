package com.school.edupoint.mapper;

import com.school.edupoint.model.StudentScore1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import java.util.List;

@Mapper
public interface StudentScore1Mapper {

    @Select("SELECT * FROM t_student_score")
    List<StudentScore1> getAllStudentScores();

    @Select("SELECT * FROM t_student_score WHERE student_id = #{studentId}")
    StudentScore1 getStudentScoreByStudentId(Integer studentId);

    @Select("SELECT * FROM t_student_score WHERE student_name = #{studentName}")
    StudentScore1 findByStudentName(String studentName);

    @Insert("INSERT INTO t_student_score (student_id, student_name, total_point, create_by, update_by) " +
            "VALUES (#{studentId}, #{studentName}, #{point}, #{createBy}, #{updateBy})")
    int insertScore(@Param("studentId") Integer studentId,
                    @Param("studentName") String studentName,
                    @Param("point") Integer point,
                    @Param("createBy") String createBy,
                    @Param("updateBy") String updateBy);

    @Update("UPDATE t_student_score SET total_point = total_point + #{point}, " +
            "update_time = NOW(), update_by = #{updateBy} " +
            "WHERE student_name = #{studentName}")
    int updateTotalPoint(@Param("studentName") String studentName,
                         @Param("point") Integer point,
                         @Param("updateBy") String updateBy);
}