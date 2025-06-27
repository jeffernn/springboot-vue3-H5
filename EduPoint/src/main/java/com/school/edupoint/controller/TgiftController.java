package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.model.Tgift;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.TgiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/gift")
public class TgiftController {

    @Autowired
    private TgiftService tfGiftService;

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public Result<IPage<Tgift>> getPage(@RequestBody Map<String, Object> params) {
        Integer pageNum = (Integer) params.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) params.getOrDefault("pageSize", 10);
        String studentName = (String) params.get("studentName");
        String giftName = (String) params.get("giftName");
        Integer status = (Integer) params.get("status");

        IPage<Tgift> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Tgift> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(studentName)) {
            wrapper.like(Tgift::getStudentName, studentName);
        }
        if (StringUtils.hasText(giftName)) {
            wrapper.like(Tgift::getGiftName, giftName);
        }
        if (status != null) {
            wrapper.eq(Tgift::getStatus, status);
        }

        IPage<Tgift> result = tfGiftService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 更新状态为“已送达”（status = 1）
     */
    @PostMapping("/update-status")
    public Result<Boolean> updateStatus(@RequestBody Map<String, Object> param) {
        Integer id = (Integer) param.get("id");
        Integer status = (Integer) param.get("status");

        Tgift gift = new Tgift();
        gift.setId(id);
        gift.setStatus(status);
        gift.setUpdateTime(new Date());
        gift.setUpdateBy("admin"); // 可替换为当前登录用户

        boolean success = tfGiftService.updateById(gift);
        return success ? Result.success(true) : Result.error(500, "更新失败");
    }

    /**
     * 删除记录
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestBody Map<String, Integer> param) {
        Integer id = param.get("id");
        boolean success = tfGiftService.removeById(id);
        return success ? Result.success(true) : Result.error(500, "删除失败");
    }
}
