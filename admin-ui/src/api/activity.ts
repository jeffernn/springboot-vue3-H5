// src/api/activity.ts
import request from '../utils/request'

export default {
  getPage(params) {
    return request({
      url: '/api/activity/page',
      method: 'POST',
      data: params,
      headers: {
        Authorization: `pc ${localStorage.getItem('token') || ''}`
      }
    })
  },
  join(data) {
    return request({
      url: '/api/student/activity/join',
      method: 'POST',
      data,
      headers: {
        Authorization: `student ${localStorage.getItem('token') || ''}`
      }
    })
  },
  checkIn(data) {
    return request({
      url: '/api/student/activity/checkin',
      method: 'POST',
      data,
      headers: {
        Authorization: `student ${localStorage.getItem('token') || ''}`
      }
    })
  },
  save(data) {
    return request({
      url: '/api/activity/save',
      method: 'POST',
      data,
      headers: {
        Authorization: `pc ${localStorage.getItem('token') || ''}`
      }
    })
  },
  deleteActivity(data) {
    return request({
      url: '/api/activity/delete',
      method: 'POST',
      data,
      headers: {
        Authorization: `pc ${localStorage.getItem('token') || ''}`
      }
    })
  },
  getById(data) {
    return request({
      url: '/api/activity/get',
      method: 'POST',
      data,
      headers: {
        Authorization: `pc ${localStorage.getItem('token') || ''}`
      }
    })
  }
}
