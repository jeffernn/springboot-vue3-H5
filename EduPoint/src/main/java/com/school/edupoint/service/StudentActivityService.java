package com.school.edupoint.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.edupoint.mapper.StudentActivityHistoryMapper;
import com.school.edupoint.model.Activity;
import com.school.edupoint.model.Student;
import com.school.edupoint.model.StudentActivityHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StudentActivityService extends ServiceImpl<StudentActivityHistoryMapper, StudentActivityHistory> {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private StudentActivityHistoryMapper historyMapper;

    // 检查学生是否已经参加过该活动
    public boolean hasJoined(Long studentId, Long activityId) {
        StudentActivityHistory history = lambdaQuery()
                .eq(StudentActivityHistory::getStudentId, studentId)
                .eq(StudentActivityHistory::getActivityId, activityId)
                .one();
        return history != null && history.getStatus() == 1;
    }

    // 检查学生是否已经完成打卡
    public boolean hasCheckedIn(Long studentId, Long activityId) {
        StudentActivityHistory history = lambdaQuery()
                .eq(StudentActivityHistory::getStudentId, studentId)
                .eq(StudentActivityHistory::getActivityId, activityId)
                .one();
        return history != null && history.getStatus() == 2;
    }

    // 学生报名活动
    public void join(Long studentId, String studentName, Long activityId) {
        // 检查是否已经参加过
        StudentActivityHistory existingHistory = lambdaQuery()
                .eq(StudentActivityHistory::getStudentId, studentId)
                .eq(StudentActivityHistory::getActivityId, activityId)
                .one();

        if (existingHistory != null) {
            if (existingHistory.getStatus() == 1) {
                throw new RuntimeException("您已经参加过该活动，不能重复参加");
            } else if (existingHistory.getStatus() == 2) {
                throw new RuntimeException("您已经完成该活动的打卡，不能重复参加");
            }
        }

        Activity activity = activityService.getById(activityId.intValue());
        StudentActivityHistory history = new StudentActivityHistory();
        history.setStudentId(studentId);
        history.setStudentName(studentName);
        history.setActivityId(activityId);
        history.setActivityName(activity.getTitle());
        history.setActivityTime(activity.getStartTime() + " ~ " + activity.getEndTime());
        history.setStatus(1); // 申请中
        save(history);
    }

    // 学生打卡活动
    public void checkIn(Long studentId, Long activityId) {
        StudentActivityHistory history = lambdaQuery()
                .eq(StudentActivityHistory::getStudentId, studentId)
                .eq(StudentActivityHistory::getActivityId, activityId)
                .one();

        if (history == null) {
            throw new RuntimeException("您还未参加该活动，无法打卡");
        }

        if (history.getStatus() == 2) {
            throw new RuntimeException("您已经完成该活动的打卡，不能重复打卡");
        }

        if (history.getStatus() != 1) {
            throw new RuntimeException("您的活动状态异常，无法打卡");
        }

        history.setStatus(2); // 已完成
        updateById(history);
    }

    public boolean save(StudentActivityHistory history) {
        // 获取活动海报
        String poster = historyMapper.getPosterByActivityName(history.getActivityName());
        history.setPoster(poster);

        // 检查是否已存在记录
        StudentActivityHistory existingHistory = lambdaQuery()
                .eq(StudentActivityHistory::getStudentId, history.getStudentId())
                .eq(StudentActivityHistory::getActivityId, history.getActivityId())
                .one();

        if (existingHistory != null) {
            // 如果存在记录，更新状态
            existingHistory.setStatus(history.getStatus());
            existingHistory.setUpdateTime(new Date());
            existingHistory.setUpdateBy(history.getUpdateBy());
            return updateById(existingHistory);
        } else {
            // 如果不存在记录，插入新记录
            return historyMapper.insert(history) > 0;
        }
    }

    public Page<StudentActivityHistory> getHistoryPage(Long studentId, int pageNum, int pageSize) {
        Page<StudentActivityHistory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<StudentActivityHistory> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(StudentActivityHistory::getStudentId, studentId);
        }

        // 获取分页数据
        Page<StudentActivityHistory> result = historyMapper.selectPage(page, wrapper);

        // 为每条记录填充poster
        result.getRecords().forEach(history -> {
            String poster = historyMapper.getPosterByActivityName(history.getActivityName());
            history.setPoster(poster);
        });

        return result;
    }

    public List<StudentActivityHistory> getHistoryList(Long studentId) {
        LambdaQueryWrapper<StudentActivityHistory> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(StudentActivityHistory::getStudentId, studentId);
        }

        // 获取列表数据
        List<StudentActivityHistory> list = historyMapper.selectList(wrapper);

        // 为每条记录填充poster
        list.forEach(history -> {
            String poster = historyMapper.getPosterByActivityName(history.getActivityName());
            history.setPoster(poster);
        });

        return list;
    }

    public StudentActivityHistoryMapper getHistoryMapper() {
        return historyMapper;
    }

}
