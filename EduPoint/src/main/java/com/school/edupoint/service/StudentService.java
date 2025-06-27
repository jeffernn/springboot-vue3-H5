package com.school.edupoint.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.config.EduContext;
import com.school.edupoint.mapper.StudentMapper;
import com.school.edupoint.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/28 17:42
 */
@Service
public class StudentService {
    @Autowired
    private EduContext eduContext;
    @Autowired
    private StudentMapper studentMapper;

    public Student login(String username, String password) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getUsername, username);
        Student user = studentMapper.selectOne(queryWrapper);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public Student getInfo(Integer id) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getId, id);
        return studentMapper.selectOne(queryWrapper);
    }

    public IPage<Student> selectPage(String username, int pageNum, int pageSize) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Student::getUsername, username);
        return studentMapper.selectPage(page, wrapper);
    }

    /**
     * 新增或更新
     *
     * @param student 实体
     * @return 提示
     */
    public Student save(Student student) {
        if (student.getId() == null) {
            student.setCreateBy(eduContext.getUser().getId());
            student.setCreateTime(new Date());
            studentMapper.insert(student);
        } else {
            student.setUpdateBy(eduContext.getUser().getId());
            student.setUpdateTime(new Date());
            studentMapper.updateById(student);
        }

        return student;
    }

    public void delete(Integer id) {
        studentMapper.deleteById(id);
    }

    /**
     * 获取第一个学生账号
     * @return 学生信息
     */
    public Student getFirstStudent() {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.last("LIMIT 1");
        return studentMapper.selectOne(queryWrapper);
    }

    /**
     * 获取默认学生账号，用于创建礼物兑换记录
     * @return 默认学生信息
     */
    public Student getDefaultStudent() {
        // 首先尝试获取第一个学生
        Student student = getFirstStudent();
        if (student != null) {
            return student;
        }

        // 如果没有任何学生记录，返回null
        return null;
    }
}
