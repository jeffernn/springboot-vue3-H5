package com.school.edupoint.interceptor;

import com.school.edupoint.config.EduContext;
import com.school.edupoint.token.TokenManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/29 11:55
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private EduContext eduContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 放行 /upload/** 的请求，不进行 token 校验
        if (uri.startsWith("/upload/")) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (StringUtils.isEmpty(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
            return false;
        }

        String[] parts = token.split(" ");
        if (parts.length < 2) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token 格式错误，请使用 '类型 token' 格式");
            return false;
        }

        String tokenType = parts[0];
        String actualToken = parts[1];

        if ("pc".equals(tokenType)) {
            if (!TokenManager.TOKEN_MAP.containsKey(actualToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
                return false;
            }
            eduContext.setUser(TokenManager.get(actualToken));
            eduContext.setToken(actualToken);
        } else if ("student".equals(tokenType)) {
            if (!TokenManager.TOKEN_STUDENT_MAP.containsKey(actualToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
                return false;
            }
            eduContext.setStudent(TokenManager.getStudent(actualToken));
            eduContext.setToken(actualToken);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "不支持的 token 类型");
            return false;
        }

        return true;
    }


}