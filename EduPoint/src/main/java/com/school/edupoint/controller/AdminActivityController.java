package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.edupoint.model.StudentActivityHistory;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.AdminActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/activity/history")
public class AdminActivityController {

    @Autowired
    private AdminActivityService adminActivityService;

    /**
     * 获取所有学生活动记录（分页）
     */
    @PostMapping("/page")
    public Result<IPage<StudentActivityHistory>> getAllHistory(@RequestBody Map<String, Object> param) {
        int pageNum = (int) param.get("pageNum");
        int pageSize = (int) param.get("pageSize");

        return Result.success(adminActivityService.getAllHistory(pageNum, pageSize));
    }

    /**
     * 根据学生ID获取活动记录（分页）
     */
    @PostMapping("/student/page")
    public Result<IPage<StudentActivityHistory>> getHistoryByStudentId(@RequestBody Map<String, Object> param) {
        Integer studentId = (Integer) param.get("studentId");
        int pageNum = (int) param.get("pageNum");
        int pageSize = (int) param.get("pageSize");

        return Result.success(adminActivityService.getHistoryByStudentId(studentId, pageNum, pageSize));
    }

    /**
     * 根据活动ID获取报名记录（分页）
     */
    @PostMapping("/activity/page")
    public Result<IPage<StudentActivityHistory>> getHistoryByActivityId(@RequestBody Map<String, Object> param) {
        Integer activityId = (Integer) param.get("activityId");
        int pageNum = (int) param.get("pageNum");
        int pageSize = (int) param.get("pageSize");

        return Result.success(adminActivityService.getHistoryByActivityId(activityId, pageNum, pageSize));
    }
    /**
     * 编辑学生活动记录
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody StudentActivityHistory history) {
        if (history.getId() == null) {
            return Result.error(400, "ID不能为空");
        }
        adminActivityService.updateStudentActivity(history);
        return Result.success(null);
    }

    /**
     * 删除学生活动记录
     */
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Integer> param) {
        Integer id = param.get("id");
        if (id == null) {
            return Result.error(400, "ID不能为空");
        }
        adminActivityService.deleteStudentActivity(id);
        return Result.success(null);
    }
    @PostMapping("/query")
    public Result<IPage<StudentActivityHistory>> query(@RequestBody Map<String, Object> param) {
        Integer pageNum = (Integer) param.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) param.getOrDefault("pageSize", 10);

        Long studentId = null;
        if (param.containsKey("studentId")) {
            Object sid = param.get("studentId");
            if (sid instanceof Number) {
                studentId = ((Number) sid).longValue();
            } else if (sid instanceof String) {
                try {
                    studentId = Long.parseLong((String) sid);
                } catch (NumberFormatException ignored) {}
            }
        }

        Long activityId = null;
        if (param.containsKey("activityId")) {
            Object aid = param.get("activityId");
            if (aid instanceof Number) {
                activityId = ((Number) aid).longValue();
            } else if (aid instanceof String) {
                try {
                    activityId = Long.parseLong((String) aid);
                } catch (NumberFormatException ignored) {}
            }
        }

        IPage<StudentActivityHistory> page = adminActivityService.queryPage(pageNum, pageSize, studentId, activityId);
        return Result.success(page);
    }
    /**
     * 获取学生活动状态统计（用于饼图）
     */
    @PostMapping("/status/statistics")
    public Result<Map<String, Integer>> getActivityStatusStatistics() {
        return Result.success(adminActivityService.getActivityStatusStatistics());
    }

}
