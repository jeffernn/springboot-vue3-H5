package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.edupoint.config.EduContext;
import com.school.edupoint.model.Activity;
import com.school.edupoint.model.Student;
import com.school.edupoint.model.User;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.StudentService;
import com.school.edupoint.token.TokenManager;
import com.school.edupoint.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/28 17:44
 */
@RestController
@RequestMapping("/api")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private EduContext eduContext;

    @PostMapping("/student/login")
    public Result<Map> login(@RequestBody Map<String, Object> param) throws Exception {
        Student student = studentService.login(param.get("username").toString(), param.get("password").toString());
        if (student == null) {
            throw new Exception("用户名或密码错误");
        }
        var map = ObjectUtils.objToMap(student);
        String token = UUID.randomUUID().toString();
        TokenManager.putStudent(token, student);
        map.put("accessToken", token);
        map.put("tokenType", "student");
        return Result.success(map);
    }

    @GetMapping("/student/info/get")
    public Result<Student> getInfo() {
        return Result.success(studentService.getInfo(eduContext.getStudent().getId()));
    }

    @PostMapping("/student/logout")
    public Result<Map> logout() {
        TokenManager.remove(eduContext.getToken());
        return Result.success(null);
    }

    @PostMapping("/student/page")
    public Result<IPage<Student>> getUserPage(@RequestBody Map<String, Object> param) throws Exception {
        String username = (String) param.get("username");
        int pageNum = (int) param.get("pageNum");
        int pageSize = (int) param.get("pageSize");

        return Result.success(studentService.selectPage(username, pageNum, pageSize));
    }

    @PostMapping("/student/save")
    public Result<Student> saveUser(@RequestBody Student student) {
        return Result.success(studentService.save(student));
    }

    @PostMapping("/student/delete")
    public Result<User> deleteUser(@RequestBody Student student) {
        studentService.delete(student.getId());
        return Result.success(null);
    }

    @PostMapping("/student/get")
    public Result<Student> getUser(@RequestBody Student student) {
        return Result.success(studentService.getInfo(student.getId()));
    }
}