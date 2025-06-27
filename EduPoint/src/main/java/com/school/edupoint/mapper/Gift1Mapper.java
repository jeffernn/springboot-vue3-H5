package com.school.edupoint.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.model.Gift;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Gift1Mapper extends BaseMapper<Gift> {
    IPage<Gift> getPage(Page<Gift> page, LambdaQueryWrapper<Gift> wrapper);

    /**
     * 获取礼物详情
     * @param id 礼物ID
     * @return 礼物信息
     */
    default Gift getById(Integer id) {
        // 使用MyBatis-Plus提供的selectById方法
        return this.selectById(id);
    }

    /**
     * 保存或更新礼物信息
     * @param gift 礼物信息
     * @return 是否成功
     */
    default boolean saveOrUpdate(Gift gift) {
        if (gift.getId() == null) {
            // 新增
            return this.insert(gift) > 0;
        } else {
            // 更新
            return this.updateById(gift) > 0;
        }
    }

    /**
     * 删除礼物
     * @param id 礼物ID
     * @return 是否成功
     */
    default boolean removeById(Integer id) {
        // 使用MyBatis-Plus提供的deleteById方法
        return this.deleteById(id) > 0;
    }

    /**
     * 获取默认礼物，用于创建礼物兑换记录
     * @return 默认礼物信息
     */
    default Gift getDefaultGift() {
        // 使用MyBatis-Plus的selectOne方法获取第一条记录
        LambdaQueryWrapper<Gift> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.last("LIMIT 1");
        return this.selectOne(queryWrapper);
    }
}