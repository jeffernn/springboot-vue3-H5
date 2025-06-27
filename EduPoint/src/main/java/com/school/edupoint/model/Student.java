package com.school.edupoint.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/29 19:12
 */
@Data
@TableName("t_student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;

    // 标准字段
    private Date createTime;
    private Date updateTime;
    private Integer createBy;
    private Integer updateBy;
}
