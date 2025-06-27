package com.school.edupoint.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_gift")
public class Gift {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("point")
    private Integer point;

    @TableField("description")
    private String description;

    @TableField("image_url")
    @JsonProperty("imageUrl") // 修改为小驼峰命名
    private String imageUrl;

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
    @TableField("stock")
    private Integer stock;
}
