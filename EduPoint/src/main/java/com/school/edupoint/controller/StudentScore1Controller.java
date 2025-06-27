package com.school.edupoint.controller;

import com.school.edupoint.model.StudentScore1;
import com.school.edupoint.service.impl.StudentScore1Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-scores")
public class StudentScore1Controller {

    @Autowired
    private StudentScore1Impl studentScoreService;

    @GetMapping
    public List<StudentScore1> getAllStudentScores() {
        return studentScoreService.getAllStudentScores();
    }

    @GetMapping("/{studentId}")
    public StudentScore1 getStudentScore(@PathVariable Integer studentId) {
        return studentScoreService.getStudentScoreByStudentId(studentId);
    }
}