package com.school.edupoint.service.impl;

import com.school.edupoint.mapper.StudentScore1Mapper;
import com.school.edupoint.model.StudentScore1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentScore1Impl {

    @Autowired
    private StudentScore1Mapper studentScore1Mapper;

    public List<StudentScore1> getAllStudentScores() {
        return studentScore1Mapper.getAllStudentScores();
    }

    public StudentScore1 getStudentScoreByStudentId(Integer studentId) {
        return studentScore1Mapper.getStudentScoreByStudentId(studentId);
    }

    @Transactional
    public boolean updateTotalPoint(String studentName, Integer point, String updateBy) {
        // 先查询是否存在该学生的积分记录
        StudentScore1 existingScore = studentScore1Mapper.findByStudentName(studentName);

        if (existingScore == null) {
            // 如果不存在，创建新记录
            // 从updateBy中获取学生ID（因为updateBy存储的是学生ID的字符串形式）
            Integer studentId = Integer.parseInt(updateBy);

            int insertResult = studentScore1Mapper.insertScore(
                    studentId,
                    studentName,
                    point,
                    updateBy,
                    updateBy
            );
            return insertResult > 0;
        } else {
            // 如果存在，更新积分
            return studentScore1Mapper.updateTotalPoint(studentName, point, updateBy) > 0;
        }
    }
}