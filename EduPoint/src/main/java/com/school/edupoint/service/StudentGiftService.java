package com.school.edupoint.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.config.EduContext;
import com.school.edupoint.mapper.StudentGiftMapper;
import com.school.edupoint.model.StudentGift;
import com.school.edupoint.model.User;
import com.school.edupoint.service.impl.StudentScore1Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Service
public class StudentGiftService {
    @Autowired
    private StudentGiftMapper studentGiftMapper;

    @Autowired
    private EduContext eduContext;

    @Autowired
    private StudentScore1Impl studentScoreService;

    /**
     * 分页查询礼物兑换记录
     */
    public IPage<StudentGift> getPage(Integer pageNum, Integer pageSize, Integer studentId, Integer giftId, Integer status) {
        Page<StudentGift> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<StudentGift> wrapper = new LambdaQueryWrapper<>();

        if (studentId != null) {
            wrapper.eq(StudentGift::getStudentId, studentId);
        }

        if (giftId != null) {
            wrapper.eq(StudentGift::getGiftId, giftId);
        }

        if (status != null) {
            wrapper.eq(StudentGift::getStatus, status);
        }

        wrapper.orderByDesc(StudentGift::getCreateTime);

        return studentGiftMapper.getPage(page, wrapper);
    }

    /**
     * 根据ID获取兑换记录详情
     */
    public StudentGift getById(Integer id) {
        return studentGiftMapper.getById(id);
    }

    /**
     * 检查记录是否存在
     */
    public boolean checkRecordExists(Integer id) {
        StudentGift gift = studentGiftMapper.getById(id);
        return gift != null;
    }

    /**
     * 更新兑换状态
     */
    @Transactional
    public boolean updateStatus(Integer id, Integer status, String updateBy) {
        // 先检查记录是否存在
        if (!checkRecordExists(id)) {
            return false;
        }
        return studentGiftMapper.updateStatus(id, status, updateBy);
    }

    /**
     * 保存或更新兑换记录
     */
    @Transactional
    public StudentGift save(StudentGift studentGift) {
        String userId = "system";
        try {
            User currentUser = eduContext.getUser();
            if (currentUser != null) {
                userId = String.valueOf(currentUser.getId());
            }
        } catch (Exception e) {
            System.out.println("获取当前用户信息失败: " + e.getMessage());
        }

        System.out.println("开始保存礼物兑换记录: " + studentGift);

        if (studentGift.getId() == null) {
            // 新增记录
            studentGift.setCreateBy(userId);
            studentGift.setCreateTime(new Date());

            System.out.println("执行插入操作，数据: " + studentGift);
            try {
                int result = studentGiftMapper.insert(studentGift);
                System.out.println("插入结果: " + result + ", 生成的ID: " + studentGift.getId());

                if (result <= 0) {
                    System.out.println("插入失败，影响行数为0");
                    return null;
                }
            } catch (Exception e) {
                System.out.println("插入数据异常: " + e.getMessage());
                e.printStackTrace();
                return null;
            }

            // 扣减学生积分
            if (studentGift.getStudentName() != null) {
                try {
                    studentScoreService.updateTotalPoint(studentGift.getStudentName(), -studentGift.getPoint(), userId);
                } catch (Exception e) {
                    System.out.println("扣减积分失败: " + e.getMessage());
                    // 继续执行，不影响礼品兑换记录的保存
                }
            }
        } else {
            // 更新记录
            studentGift.setUpdateBy(userId);
            studentGift.setUpdateTime(new Date());

            // 先检查记录是否存在
            StudentGift existingGift = studentGiftMapper.getById(studentGift.getId());
            if (existingGift == null) {
                System.out.println("更新失败，ID为" + studentGift.getId() + "的记录不存在，尝试插入新记录");
                // 如果记录不存在，则执行插入操作
                studentGift.setId(null); // 清除ID，让数据库自动生成
                studentGift.setCreateBy(userId);
                studentGift.setCreateTime(new Date());

                try {
                    int result = studentGiftMapper.insert(studentGift);
                    System.out.println("插入结果: " + result + ", 生成的ID: " + studentGift.getId());

                    if (result <= 0) {
                        System.out.println("插入失败，影响行数为0");
                        return null;
                    }
                } catch (Exception e) {
                    System.out.println("插入数据异常: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            } else {
                System.out.println("执行更新操作，数据: " + studentGift);
                try {
                    int result = studentGiftMapper.updateById(studentGift);
                    System.out.println("更新结果: " + result);

                    if (result <= 0) {
                        System.out.println("更新失败，影响行数为0");
                        return null;
                    }
                } catch (Exception e) {
                    System.out.println("更新数据异常: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        }

        System.out.println("保存完成，返回数据: " + studentGift);
        return studentGift;
    }

    /**
     * 删除兑换记录
     */
    public boolean delete(Integer id) {
        return studentGiftMapper.removeById(id);
    }

    /**
     * 获取所有礼物兑换记录
     * @return 所有兑换记录列表
     */
    public List<StudentGift> getAllExchangeRecords() {
        System.out.println("服务层：获取所有礼物兑换记录");
        try {
            // 使用MyBatis-Plus的selectList方法获取所有记录
            LambdaQueryWrapper<StudentGift> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(StudentGift::getCreateTime); // 按创建时间降序排序
            List<StudentGift> list = studentGiftMapper.selectList(wrapper);
            System.out.println("服务层：共获取到 " + list.size() + " 条记录");
            return list;
        } catch (Exception e) {
            System.out.println("获取所有礼物兑换记录异常: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // 发生异常时返回空列表
        }
    }
}