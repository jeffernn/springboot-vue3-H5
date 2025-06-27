package com.school.edupoint.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentScore1 {
    private Integer id;
    private Integer studentId;
    private String studentName;
    private Integer totalPoint;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
}