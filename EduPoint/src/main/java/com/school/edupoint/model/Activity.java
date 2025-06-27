package com.school.edupoint.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_activity")
public class Activity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("start_time")
    private String startTime;

    @TableField("end_time")
    private String endTime;

    @TableField("point")
    private Integer point;

    @TableField("poster")
    private String poster;

    @TableField("rule_description")
    private String ruleDescription;

    @TableField("create_time")
    private Date createTime;

    @TableField("create_by")
    private Integer createBy;

    @TableField("update_time")
    private Date updateTime;

    @TableField("update_by")
    private Integer updateBy;
}
