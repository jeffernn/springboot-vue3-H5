package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.config.EduContext;
import com.school.edupoint.model.User;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.UserService;
import com.school.edupoint.token.TokenManager;
import com.school.edupoint.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/28 17:44
 */
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EduContext eduContext;

    @PostMapping("/login")
    public Result<Map> login(@RequestBody Map<String, Object> param) throws Exception {
        User user = userService.login(param.get("username").toString(), param.get("password").toString());
        if (user == null) {
            throw new Exception("用户名或密码错误");
        }
        var map = ObjectUtils.objToMap(user);
        String token = UUID.randomUUID().toString();
        TokenManager.put(token, user);
        map.put("accessToken", token);
        map.put("tokenType", "pc");
        return Result.success(map);
    }

    @GetMapping("/user/info/get")
    public Result<User> getUserInfo() {
        return Result.success(userService.getUserInfo(eduContext.getUser().getId()));
    }

    @PostMapping("/user/logout")
    public Result<Map> logout() {
        TokenManager.remove(eduContext.getUser().getUsername());
        return Result.success(new HashMap());
    }

    @PostMapping("/user/page")
    public Result<IPage<User>> getUserPage(@RequestBody Map<String, Object> param) throws Exception {
        String username = (String) param.get("username");
        int pageNum = (int) param.get("pageNum");
        int pageSize = (int) param.get("pageSize");

        return Result.success(userService.selectUserPage(username, pageNum, pageSize));
    }

    @PostMapping("/user/save")
    public Result<User> saveUser(@RequestBody User user) {
        return Result.success(userService.saveUser(user));
    }

    @PostMapping("/user/delete")
    public Result<User> deleteUser(@RequestBody User user) {
        userService.deleteUser(user.getId());
        return Result.success(null);
    }

    @PostMapping("/user/get")
    public Result<User> getUser(@RequestBody User user) {
        return Result.success(userService.getUserInfo(user.getId()));
    }
}