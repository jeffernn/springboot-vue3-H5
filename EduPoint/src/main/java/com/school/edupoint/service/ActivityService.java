package com.school.edupoint.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.config.EduContext;
import com.school.edupoint.mapper.ActivityMapper;
import com.school.edupoint.mapper.StudentActivityHistoryMapper;
import com.school.edupoint.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class ActivityService {

    @Autowired
    private EduContext eduContext;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private StudentActivityHistoryMapper historyMapper;

    public IPage<Activity> selectPage(String title, int pageNum, int pageSize) {
        Page<Activity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Activity::getTitle, title);
        }
        return activityMapper.selectPage(page, wrapper);
    }

    public Activity save(Activity activity) {
        if (activity.getId() == null) {
            activity.setCreateBy(eduContext.getUser().getId());
            activity.setCreateTime(new Date());
            activityMapper.insert(activity);
            historyMapper.updatePosterByActivityName(activity.getTitle(), activity.getPoster());
        } else {
            activity.setUpdateBy(eduContext.getUser().getId());
            activity.setUpdateTime(new Date());
            activityMapper.updateById(activity);
            historyMapper.updatePosterByActivityName(activity.getTitle(), activity.getPoster());
        }
        return activity;
    }

    public void delete(Integer id) {
        activityMapper.deleteById(id);
    }

    public Activity getById(Integer id) {
        return activityMapper.selectById(id);
    }

    public List<Activity> listAll() {
        return activityMapper.selectList(null);
    }



    /**
     * 获取 ActivityMapper（用于其他 Service 或 Controller 调用）
     */
    public ActivityMapper getActivityMapper() {
        return activityMapper;
    }
}

