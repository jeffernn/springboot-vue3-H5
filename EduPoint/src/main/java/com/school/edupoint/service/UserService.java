package com.school.edupoint.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.config.EduContext;
import com.school.edupoint.mapper.UserMapper;
import com.school.edupoint.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/28 17:42
 */
@Service
public class UserService {
    @Autowired
    private EduContext eduContext;
    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserInfo(Integer id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id);
        return userMapper.selectOne(queryWrapper);
    }

    public IPage<User> selectUserPage(String username, int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(User::getUsername, username);
        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 新增或更新
     *
     * @param user 实体
     * @return 提示
     */
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setCreateBy(eduContext.getUser().getId());
            user.setCreateTime(new Date());
            userMapper.insert(user);
        } else {
            user.setUpdateBy(eduContext.getUser().getId());
            user.setUpdateTime(new Date());
            userMapper.updateById(user);
        }

        return user;
    }

    public void deleteUser(Integer id) {
        userMapper.deleteById(id);
    }
}
