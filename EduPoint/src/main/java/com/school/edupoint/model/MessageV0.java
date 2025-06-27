package com.school.edupoint.model;


import lombok.Data;

@Data
public class MessageV0 {
    private String userId; // 接收者
    private String title; // 消息标题
    private String content; // 消息内容
    private String messageType; // 通知类型
    private String param; // 参数字符串
}
