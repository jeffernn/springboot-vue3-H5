package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.config.EduContext;
import com.school.edupoint.model.Activity;
import com.school.edupoint.model.Student;
import com.school.edupoint.model.StudentActivityHistory;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.ActivityService;
import com.school.edupoint.service.DatabaseSyncService;
import com.school.edupoint.service.StudentActivityService;
import com.school.edupoint.service.StudentService;
import com.school.edupoint.service.impl.StudentScore1Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private StudentActivityService studentActivityService;

    @Autowired
    private EduContext eduContext;

    @Autowired
    private StudentService studentService;

    @Autowired
    private DatabaseSyncService databaseSyncService;

    @Autowired
    private StudentScore1Impl studentScoreService;

    @PostMapping("/page")
    public Result<IPage<Activity>> getPage(@RequestBody Map<String, Object> param) {
        String title = (String) param.get("title");
        int pageNum = (int) param.get("pageNum");
        int pageSize = (int) param.get("pageSize");
        return Result.success(activityService.selectPage(title, pageNum, pageSize));
    }

    @PostMapping("/save")
    public Result<Activity> save(@RequestBody Activity activity) {
        Activity saved = activityService.save(activity);
        // 新增活动后推送WebSocket消息到所有学生
        com.school.edupoint.model.MessageV0 msg = new com.school.edupoint.model.MessageV0();
        msg.setTitle(activity.getTitle() + "已发布");
        msg.setMessageType("new_activity");
        com.school.edupoint.websocket.MyTextWebSocketHandler.sendToAllStudents(msg);
        return Result.success(saved);
    }

    @PostMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Object> param) {
        Integer id = (Integer) param.get("id");
        activityService.delete(id);
        return Result.success(null);
    }

    @PostMapping("/get")
    public Result<Activity> getById(@RequestBody Activity activity) {
        return Result.success(activityService.getById(activity.getId()));
    }

    @PostMapping("/listAll")
    public Result<java.util.List<Activity>> listAll() {
        return Result.success(activityService.listAll());
    }

    /**
     * 获取活动详情
     * @param id 活动ID
     * @return 活动详情
     */
    @GetMapping("/detail/{id}")
    public Result<Activity> getActivityDetail(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error(404, "活动不存在");
        }
        return Result.success(activity);
    }

    /**
     * 公开接口 - 获取活动分页列表（无需身份验证）
     * @param title 活动标题（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 活动分页数据
     */
    @GetMapping("/public/page")
    public Result<IPage<Activity>> getPublicPage(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(activityService.selectPage(title, pageNum, pageSize));
    }

    /**
     * 公开接口 - 获取活动详情（无需身份验证）
     * @param id 活动ID
     * @return 活动详情
     */
    @GetMapping("/public/get/{id}")
    public Result<Activity> getPublicById(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error(404, "活动不存在");
        }
        return Result.success(activity);
    }

    /**
     * 公开接口 - 获取所有活动列表（无需身份验证）
     * @return 活动列表
     */
    @GetMapping("/public/listAll")
    public Result<java.util.List<Activity>> getPublicListAll() {
        return Result.success(activityService.listAll());
    }

    /**
     * 学生签到接口
     * @param request 签到请求体
     * @return 签到结果
     */
    @PostMapping("/check-in")
    public Result<?> checkInActivity(@RequestBody ActivityCheckInRequest request) {
        try {
            System.out.println("接收到请求: " + request);

            // 从请求中获取登录信息
            String username = request.getUsername();
            String password = request.getPassword();

            if (username == null || password == null) {
                System.out.println("登录信息不完整");
                return Result.error(401, "请提供完整的登录信息");
            }

            // 使用登录信息进行验证
            Student loggedInStudent = studentService.login(username, password);
            if (loggedInStudent == null) {
                System.out.println("登录验证失败");
                return Result.error(401, "用户名或密码错误");
            }

            // 参数验证
            if (request.getActivityId() == null) {
                System.out.println("活动ID为空");
                return Result.error(400, "活动ID不能为空");
            }

            // 获取当前状态
            Integer status = request.getStatus();
            if (status == null) {
                status = 1; // 默认为参加状态
            }

            // 检查是否已经参加过该活动
            StudentActivityHistory existingHistory = studentActivityService.lambdaQuery()
                    .eq(StudentActivityHistory::getStudentId, loggedInStudent.getId().longValue())
                    .eq(StudentActivityHistory::getActivityId, request.getActivityId())
                    .one();

            if (status == 1) { // 参加活动
                if (existingHistory != null) {
                    if (existingHistory.getStatus() == 1) {
                        return Result.error(400, "您已经参加过该活动，不能重复参加");
                    } else if (existingHistory.getStatus() == 2) {
                        return Result.error(400, "您已经完成该活动的打卡，不能重复参加");
                    }
                }
            } else if (status == 2) { // 打卡活动
                if (existingHistory == null) {
                    return Result.error(400, "您还未参加该活动，无法打卡");
                }
                if (existingHistory.getStatus() == 2) {
                    return Result.error(400, "您已经完成该活动的打卡，不能重复打卡");
                }
                if (existingHistory.getStatus() != 1) {
                    return Result.error(400, "您的活动状态异常，无法打卡");
                }
            }

            StudentActivityHistory history = new StudentActivityHistory();
            history.setStudentId(loggedInStudent.getId().longValue());
            history.setStudentName(loggedInStudent.getUsername());
            history.setActivityId(request.getActivityId());
            history.setStatus(status);
            history.setActivityName(request.getActivityName());
            history.setActivityTime(request.getActivityTime());
            history.setPoint(request.getPoint());
            history.setCreateBy(String.valueOf(loggedInStudent.getId()));
            history.setUpdateBy(String.valueOf(loggedInStudent.getId()));
            history.setCreateTime(new Date());
            history.setUpdateTime(new Date());

            System.out.println("准备保存记录: " + history);

            boolean success = studentActivityService.save(history);
            if (!success) {
                System.out.println("保存记录失败");
                return Result.error(500, "操作失败，请稍后重试");
            }

            // 只有当status为2时才更新学生积分
            if (status == 2) {
                boolean pointSuccess = studentScoreService.updateTotalPoint(
                        loggedInStudent.getUsername(),
                        request.getPoint() != null ? request.getPoint() : 1,
                        String.valueOf(loggedInStudent.getId())
                );

                if (!pointSuccess) {
                    System.out.println("更新积分失败");
                    return Result.error(500, "打卡成功但积分更新失败");
                }
                // 打卡成功后推送WebSocket消息到所有管理员
                com.school.edupoint.model.MessageV0 msg = new com.school.edupoint.model.MessageV0();
                msg.setTitle(loggedInStudent.getUsername() + "成功打卡" + request.getActivityName());
                msg.setMessageType("activity_card");
                com.school.edupoint.websocket.MyTextWebSocketHandler.sendToAllAdmins(msg);

            }

            // 执行数据库同步
            try {
                databaseSyncService.syncActivityPoster();
                System.out.println("数据库同步成功");
            } catch (Exception e) {
                System.out.println("数据库同步失败: " + e.getMessage());
                e.printStackTrace();
                // 这里我们不返回错误，因为操作已经成功，同步失败不影响主要功能
            }

            System.out.println("操作成功");
            return Result.success(status == 1 ? "参加成功" : "打卡成功");
        } catch (Exception e) {
            System.out.println("操作过程发生错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "操作失败：" + e.getMessage());
        }
    }
}

