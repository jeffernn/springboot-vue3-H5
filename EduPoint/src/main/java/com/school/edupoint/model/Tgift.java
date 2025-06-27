package com.school.edupoint.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_student_gift")
public class Tgift {

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
    private Date createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Date updateTime;

    @TableField("update_by")
    private String updateBy;
}
