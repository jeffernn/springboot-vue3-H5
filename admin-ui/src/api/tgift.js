import request from '../utils/request'

export default {
  // 分页查询
  getPage(params) {
    return request({
      url: '/api/gift/page',
      method: 'POST',
      data: params,
      headers: {
        Authorization: `pc ${localStorage.getItem('token') || ''}`
      }
    })
  },

  // 更新状态
  updateStatus(data) {
    return request({
      url: '/api/gift/update-status',
      method: 'POST',
      data,
      headers: {
        Authorization: `pc ${localStorage.getItem('token') || ''}`
      }
    })
  },

  // 删除记录
  deleteGift(data) {
    return request({
      url: '/api/gift/delete',
      method: 'POST',
      data,
      headers: {
        Authorization: `pc ${localStorage.getItem('token') || ''}`
      }
    })
  }
}
