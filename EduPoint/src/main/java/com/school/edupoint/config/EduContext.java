package com.school.edupoint.config;

import com.school.edupoint.model.Student;
import com.school.edupoint.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/29 11:59
 */
@Component
public class EduContext {
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private Student student;

    @Setter
    @Getter
    private String token;
}
