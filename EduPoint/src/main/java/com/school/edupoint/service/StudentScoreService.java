package com.school.edupoint.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.edupoint.mapper.StudentScoreMapper;
import com.school.edupoint.model.StudentScore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StudentScoreService extends ServiceImpl<StudentScoreMapper, StudentScore> {
    public List<Map<String, Object>> getMonthlyPointTrend() {
        return baseMapper.selectMonthlyPointTrend();
    }

}
