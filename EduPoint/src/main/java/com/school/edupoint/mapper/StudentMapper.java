package com.school.edupoint.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.edupoint.model.Student;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/28 17:40
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}