package com.school.edupoint.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.model.StudentGift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface StudentGiftMapper extends BaseMapper<StudentGift> {
    /**
     * 分页查询
     */
    default IPage<StudentGift> getPage(Page<StudentGift> page, LambdaQueryWrapper<StudentGift> wrapper) {
        return this.selectPage(page, wrapper);
    }

    /**
     * 根据ID获取
     */
    default StudentGift getById(Integer id) {
        return this.selectById(id);
    }

    /**
     * 保存或更新
     */
    default boolean saveOrUpdate(StudentGift studentGift) {
        if (studentGift.getId() == null) {
            return this.insert(studentGift) > 0;
        } else {
            return this.updateById(studentGift) > 0;
        }
    }

    /**
     * 删除
     */
    default boolean removeById(Integer id) {
        return this.deleteById(id) > 0;
    }

    /**
     * 更新状态
     */
    boolean updateStatus(@Param("id") Integer id, @Param("status") Integer status, @Param("updateBy") String updateBy);
}