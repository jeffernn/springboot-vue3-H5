package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.model.StudentScore;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.StudentScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/score")
public class StudentScoreController {

    @Autowired
    private StudentScoreService studentScoreService;

    /**
     * 分页查询学生积分
     */
    @PostMapping("/page")
    public Result<IPage<StudentScore>> getPage(@RequestBody Map<String, Object> param) {
        Integer pageNum = (Integer) param.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) param.getOrDefault("pageSize", 10);

        Integer studentId = null;
        if (param.containsKey("studentId")) {
            Object sid = param.get("studentId");
            if (sid instanceof Number) {
                studentId = ((Number) sid).intValue();
            } else if (sid instanceof String) {
                try {
                    studentId = Integer.parseInt((String) sid);
                } catch (NumberFormatException ignored) {}
            }
        }

        IPage<StudentScore> page = new Page<>(pageNum, pageSize);
        if (studentId != null) {
            return Result.success(studentScoreService.page(page, new LambdaQueryWrapper<StudentScore>()
                    .eq(StudentScore::getStudentId, studentId)));
        } else {
            return Result.success(studentScoreService.page(page));
        }
    }

    /**
     * 根据学生ID获取积分详情
     */
    @PostMapping("/get")
    public Result<StudentScore> getById(@RequestBody Map<String, Object> param) {
        Integer studentId = (Integer) param.get("studentId");
        StudentScore score = studentScoreService.lambdaQuery()
                .eq(StudentScore::getStudentId, studentId)
                .one();
        return Result.success(score);
    }

    /**
     * 更新积分
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody StudentScore score) {
        if (score.getId() == null) {
            return Result.error(400, "ID不能为空");
        }
        studentScoreService.updateById(score);
        return Result.success(null);
    }

    /**
     * 删除积分记录
     */
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Integer> param) {
        Integer id = param.get("id");
        if (id == null) {
            return Result.error(400, "ID不能为空");
        }
        studentScoreService.removeById(id);
        return Result.success(null);
    }

    /**
     * 获取每月积分趋势数据（用于柱状图）
     */
    @PostMapping("/trend/monthly")
    public Result<List<Map<String, Object>>> getMonthlyPointTrend() {
        return Result.success(studentScoreService.getMonthlyPointTrend());
    }
}
