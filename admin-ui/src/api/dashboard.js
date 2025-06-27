import request from '../utils/request'

export default {
  // 获取首页总览数据
  getSummary() {
    return request({
      url: '/api/activity/summary',
      method: 'GET'
    })
  },

  // 获取活动统计图表数据
  getActivityChartData() {
    return request({
      url: '/api/activity/chart-data',
      method: 'GET'
    })
  },

  // 获取积分排行榜
  getStudentRank() {
    return request({
      url: '/api/student/rank',
      method: 'GET'
    })
  },

  // 获取学生活动明细
  getStudentActivityHistory(params) {
    return request({
      url: '/api/student/history',
      method: 'GET',
      params
    })
  }
}
