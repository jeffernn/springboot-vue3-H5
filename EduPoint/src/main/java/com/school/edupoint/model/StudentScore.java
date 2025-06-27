package com.school.edupoint.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
@TableName("t_student_score")
public class StudentScore {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("student_id")
    private Integer studentId;

    @TableField("student_name")
    private String studentName;

    @TableField("total_point")
    private Integer totalPoint;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField("create_by")
    private Integer createBy;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField("update_by")
    private Integer updateBy;
}
