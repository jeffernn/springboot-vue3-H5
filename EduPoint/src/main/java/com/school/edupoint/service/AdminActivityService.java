package com.school.edupoint.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.edupoint.mapper.StudentActivityHistoryMapper;
import com.school.edupoint.model.StudentActivityHistory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class AdminActivityService extends ServiceImpl<StudentActivityHistoryMapper, StudentActivityHistory> {

    /**
     * 分页查询所有学生活动记录
     */
    public Page<StudentActivityHistory> getAllHistory(int pageNum, int pageSize) {
        return page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<>());
    }

    /**
     * 根据学生ID分页查询活动记录
     */
    public Page<StudentActivityHistory> getHistoryByStudentId(Integer studentId, int pageNum, int pageSize) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<StudentActivityHistory>()
                        .eq(StudentActivityHistory::getStudentId, studentId));
    }

    /**
     * 根据活动ID分页查询报名记录
     */
    public Page<StudentActivityHistory> getHistoryByActivityId(Integer activityId, int pageNum, int pageSize) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<StudentActivityHistory>()
                        .eq(StudentActivityHistory::getActivityId, activityId));
    }
    /**
     * 编辑学生活动记录
     */
    public boolean updateStudentActivity(StudentActivityHistory history) {
        return updateById(history);
    }
    /**
     * 删除学生活动记录
     */
    public boolean deleteStudentActivity(Integer id) {
        return removeById(id);
    }
    public Page<StudentActivityHistory> queryPage(Integer pageNum, Integer pageSize,
                                                  Long studentId, Long activityId) {
        LambdaQueryWrapper<StudentActivityHistory> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(StudentActivityHistory::getStudentId, studentId);
        }
        if (activityId != null) {
            wrapper.eq(StudentActivityHistory::getActivityId, activityId);
        }

        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public Map<String, Integer> getActivityStatusStatistics() {
        List<Map<String, Object>> result = baseMapper.selectStatusStatistics();

        Map<Integer, String> statusMap = Map.of(
                1, "申请中",
                2, "已完成",
                3, "未完成"
        );

        return result.stream()
                .collect(Collectors.toMap(
                        map -> statusMap.getOrDefault(map.get("status"), "未知"),
                        map -> ((Number) map.get("count")).intValue(),
                        (existing, replacement) -> existing
                ));
    }

}
