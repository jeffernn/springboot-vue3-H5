package com.school.edupoint.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_student_gift")
public class StudentGift {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("student_id")
    private Integer studentId;

    @TableField("student_name")
    private String studentName;

    @TableField("gift_id")
    private Integer giftId;

    @TableField("gift_name")
    private String giftName;

    @TableField("image_url")
    @JsonProperty("imageUrl")
    private String imageUrl;

    @TableField("point")
    private Integer point;

    @TableField("status")
    private Integer status;

    @TableField("receive_name")
    private String receiveName;

    @TableField("receive_phone")
    private String receivePhone;

    @TableField("address")
    private String address;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField("update_by")
    private String updateBy;
}