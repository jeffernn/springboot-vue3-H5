package com.school.edupoint.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/28 17:38
 */
@Data
@TableName("t_user")
public class User {
    private Integer id;
    private String username;
    private String password;

    // 标准字段
    private Date createTime;
    private Date updateTime;
    private Integer createBy;
    private Integer updateBy;
}
