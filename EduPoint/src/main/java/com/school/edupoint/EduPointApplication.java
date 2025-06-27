package com.school.edupoint;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.school.edupoint.mapper")
public class EduPointApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduPointApplication.class, args);
    }

}
