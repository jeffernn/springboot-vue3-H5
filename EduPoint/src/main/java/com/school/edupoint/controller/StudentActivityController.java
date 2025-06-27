package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.model.Activity;
import com.school.edupoint.model.StudentActivityHistory;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.ActivityService;
import com.school.edupoint.service.StudentActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/activity")
public class StudentActivityController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private StudentActivityService studentActivityService;

    // 学生查看活动列表
    @PostMapping("/list")
    public Result<Page<Activity>> list(@RequestBody Map<String, Object> param) {
        String title = (String) param.get("title");
        int pageNum = (int) param.get("pageNum");
        int pageSize = (int) param.get("pageSize");

        // 查询所有活动，也可以根据业务逻辑限制学生可见活动
        Page<Activity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Activity::getTitle, title);
        }
        return Result.success(activityService.getActivityMapper().selectPage(page, wrapper));
    }

    // 学生报名活动
    @PostMapping("/join")
    public Result<?> join(@RequestBody Map<String, Object> param) {
        Long studentId = ((Number) param.get("studentId")).longValue();
        Long activityId = ((Number) param.get("activityId")).longValue();
        String studentName = (String) param.get("studentName");

        studentActivityService.join(studentId, studentName, activityId);
        return Result.success(null);
    }

    // 学生打卡活动
    @PostMapping("/checkin")
    public Result<?> checkIn(@RequestBody Map<String, Object> param) {
        Long studentId = ((Number) param.get("studentId")).longValue();
        Long activityId = ((Number) param.get("activityId")).longValue();
        studentActivityService.checkIn(studentId, activityId);
        return Result.success(null);
    }
    // 查看我报名的活动
    @PostMapping("/history")
    public Result<Page<StudentActivityHistory>> getHistory(@RequestBody Map<String, Object> param) {
        Long studentId = ((Number) param.get("studentId")).longValue();
        int pageNum = (int) param.get("pageNum");
        int pageSize = (int) param.get("pageSize");

        Page<StudentActivityHistory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<StudentActivityHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentActivityHistory::getStudentId, studentId);

        return Result.success(studentActivityService.getHistoryMapper().selectPage(page, wrapper));
    }

}
