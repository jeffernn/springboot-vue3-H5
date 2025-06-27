// src/api/studentScore.js
import request from '@/utils/request'

export default {
  // 查询分页数据
  queryPage(data) {
    return request({
      url: '/api/admin/score/page',
      method: 'post',
      data
    })
  },

  // 提交编辑数据
  updateStudentScore(data) {
    return request({
      url: '/api/admin/score/update',
      method: 'post',
      data
    })
  },

  // 删除记录
  deleteStudentScore(data) {
    return request({
      url: '/api/admin/score/delete',
      method: 'post',
      data
    })
  },
  // 新增：获取每月积分趋势
  getMonthlyPointTrend() {
    return request({
      url: '/api/admin/score/trend/monthly',
      method: 'post'
    })
  }
}
