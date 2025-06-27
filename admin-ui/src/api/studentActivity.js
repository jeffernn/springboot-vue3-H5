// src/api/studentActivity.js
import request from '@/utils/request'

export default {
  // 查询分页数据
  queryHistory(data) {
    return request({
      url: '/api/admin/activity/history/query',
      method: 'post',
      data
    })
  },

  // 提交编辑数据
  updateStudentActivity(data) {
    return request({
      url: '/api/admin/activity/history/update',
      method: 'post',
      data
    })
  },
  // 新增：删除记录
  deleteStudentActivity(data) {
    return request({
      url: '/api/admin/activity/history/delete',
      method: 'post',
      data
    })
  },
  // 新增：获取活动状态统计
  getActivityStatusStatistics() {
    return request({
      url: '/api/admin/activity/history/status/statistics',
      method: 'post'
    })
  }
}
