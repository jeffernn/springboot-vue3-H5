package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.model.StudentActivityHistory;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.StudentActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生活动历史记录控制器
 * 使用说明：
 * 1. 所有接口都需要在请求头中添加Authorization token
 * 2. 格式为：Authorization: "pc token" 或 "student token"
 * 3. 例如：Authorization: "pc 123456" 或 "student 123456"
 */
@RestController
@RequestMapping("/api/student-activity-history")
public class StudentActivityHistoryController {

    @Autowired
    private StudentActivityService studentActivityService;

    /**
     * 获取所有学生活动历史记录
     * 需要管理员权限（pc token）
     */
    @GetMapping
    public Result<List<StudentActivityHistory>> getAllHistory() {
        return Result.success(studentActivityService.getHistoryList(null));
    }

    /**
     * 获取指定学生的活动历史记录
     * 需要学生权限（student token）或管理员权限（pc token）
     */
    @GetMapping("/{studentId}")
    public Result<List<StudentActivityHistory>> getHistoryByStudentId(@PathVariable Long studentId) {
        return Result.success(studentActivityService.getHistoryList(studentId));
    }

    /**
     * 分页获取学生活动历史记录
     * 需要管理员权限（pc token）
     * 支持按学生ID、学生姓名、活动状态进行筛选
     */
    @GetMapping("/page")
    public Result<Page<StudentActivityHistory>> getHistoryPage(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        return Result.success(studentActivityService.getHistoryPage(studentId, pageNum, pageSize));
    }
}