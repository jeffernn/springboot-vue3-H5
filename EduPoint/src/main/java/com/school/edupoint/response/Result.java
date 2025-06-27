package com.school.edupoint.response;

import lombok.Data;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/28 20:54
 */
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 常用静态方法
    public static <T> Result<T> success(T data) {
        return new Result<T>(0, "操作成功", data);
    }


    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<T>(code, msg, null);
    }
}
