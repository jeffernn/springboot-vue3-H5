package com.school.edupoint.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;




@Data
@TableName("t_student_activity_history")
public class StudentActivityHistory {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("student_id")
    private Long studentId;

    @TableField("student_name")
    private String studentName;

    @TableField("activity_id")
    private Long activityId;

    @TableField("activity_name")
    private String activityName;

    private Integer status; // 1=申请中, 2=已完成, 3=未完成

    private Integer point;


    @TableField("activity_time")
    private String activityTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_by")
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField("poster")
    private String poster;

    // 可选：添加状态转义方法

}
