package com.school.edupoint.token;

import com.school.edupoint.model.Student;
import com.school.edupoint.model.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/29 11:39
 */
public class TokenManager {
    // token -> 用户信息
    public static final ConcurrentHashMap<String, User> TOKEN_MAP = new ConcurrentHashMap<>();

    // token -> 用户信息
    public static final ConcurrentHashMap<String, Student> TOKEN_STUDENT_MAP = new ConcurrentHashMap<>();

    public static void put(String token, User user) {
        TOKEN_MAP.put(token, user);
    }

    public static User get(String token) {
        return TOKEN_MAP.get(token);
    }

    public static void remove(String token) {
        TOKEN_MAP.remove(token);
    }

    // 学生登录系统
    public static void putStudent(String token, Student student) {
        TOKEN_STUDENT_MAP.put(token, student);
    }

    public static Student getStudent(String token) {
        return TOKEN_STUDENT_MAP.get(token);
    }

    public static void removeStudent(String token) {
        TOKEN_STUDENT_MAP.remove(token);
    }
}
