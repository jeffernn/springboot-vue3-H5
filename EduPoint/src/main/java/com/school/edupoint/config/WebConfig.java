package com.school.edupoint.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.school.edupoint.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/29 11:57
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .excludePathPatterns("/api/login") // 登录接口不拦截
                .excludePathPatterns("/api/student/login") // 登录接口不拦截
                .excludePathPatterns("/api/upload/**") // 放行上传接口
                .excludePathPatterns("/api/activity/public/**") // 放行活动公开接口
                .excludePathPatterns("/api/activity/check-in") // 放行上传接口
                .excludePathPatterns("/api/student-scores") // 放行上传接口
                .excludePathPatterns("/api/gift/list") // 放行上传接口
                .excludePathPatterns("/api/admin/gift") // 放行上传接口
                .excludePathPatterns("/api/admin/gift/exchange/list") // 放行上传接口
                .excludePathPatterns("/api/admin/gift/exchange") // 放行上传接口
                .excludePathPatterns("/api/admin/gift/check-stock-and-exchange") // 放行上传接口
                .excludePathPatterns("/api/student-activity-history/**") // 放行学生活动历史记录接口
                .addPathPatterns("/**"); // 其它接口都要token
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 假设您的API路径都以 /api 开头
                .allowedOrigins("http://localhost:5173", "http://localhost:3000","http://192.168.43.223:5173","http://192.168.110.204:5173","http://192.168.110.204:3000","http://192.168.110.204:3000") // 同时允许这两个地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许发送认证信息（如Cookie或HTTP认证）
                .maxAge(3600); // 预检请求的缓存时间（秒）
        // 新增对 /upload/** 的 CORS 支持
        registry.addMapping("/upload/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000", "http://192.168.43.223:5173", "http://192.168.110.204:5173","http://192.168.110.204:3000","http://192.168.110.204:3000")
                .allowedMethods("GET", "HEAD")
                .allowedHeaders("Content-Type", "Accept")
                .exposedHeaders("Content-Length")
                .allowCredentials(true);
    }
    /**
     * 静态资源映射配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:upload/");
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
