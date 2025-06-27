package com.school.edupoint.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.edupoint.model.MessageV0;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

public class MyTextWebSocketHandler extends TextWebSocketHandler {
    // userId -> session
    public static ConcurrentHashMap<String, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 连接建立后，等待认证
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("收到WebSocket消息: " + payload);
        MessageV0 msg = objectMapper.readValue(payload, MessageV0.class);

        // 认证消息
        if ("PC_Auth".equals(msg.getMessageType()) || "Student_Auth".equals(msg.getMessageType())) {
            userSessionMap.put(msg.getUserId(), session);
            System.out.println("认证成功: " + msg.getUserId());
            session.sendMessage(new TextMessage("{\"messageType\":\"auth_success\"}"));
            return;
        }
        // 新增：学生登录消息，群发给所有管理员
        if ("student_login".equals(msg.getMessageType())) {
            sendToAllAdmins(msg);
            System.out.println("收到学生登录消息，已通知所有管理员: " + msg.getContent());
            return;
        }
        // 其他消息类型可根据需要处理
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        userSessionMap.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }

    // 静态方法：推送消息
    public static void sendToUser(String userId, MessageV0 msg) {
        System.out.println("推送消息给: " + userId + " 内容: " + msg.getTitle() + " 类型: " + msg.getMessageType());
        WebSocketSession session = userSessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
                System.out.println("推送成功: " + userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("推送失败，未找到session或session已关闭: " + userId);
        }
    }

    // 群发给所有学生
    public static void sendToAllStudents(MessageV0 msg) {
        System.out.println("群发给所有学生: " + msg.getTitle());
        userSessionMap.forEach((userId, session) -> {
            sendToUser(userId, msg);
        });
    }

    // 群发给所有管理员
    public static void sendToAllAdmins(MessageV0 msg) {
        System.out.println("群发给所有管理员: " + msg.getTitle());
        userSessionMap.forEach((userId, session) -> {
            if (!userId.matches("\\d+")) {
                sendToUser(userId, msg);
            }
        });
    }
}