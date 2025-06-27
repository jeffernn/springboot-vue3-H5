class WebSocketService {
  constructor(url) {
    this.url = url
    this.socketTask = null
    this.callbacks = {}
    this.messageQueue = []
    this.connect()
  }

  connect() {
    if (this.socketTask) return
    this.socketTask = uni.connectSocket({
      url: this.url,
      success: () => {
        console.log('WebSocket connecting...')
      }
    })
    this.socketTask.onOpen(() => {
      console.log('WebSocket opened')
      this.send({
        userId: 'h5user',
        messageType: 'Student_Auth'
      })
      console.log('H5端自动认证消息已发送')
    })
    this.socketTask.onMessage((res) => {
      console.log('收到WebSocket消息:', res.data)
      const data = JSON.parse(res.data)
      if (data.messageType && this.callbacks[data.messageType]) {
        this.callbacks[data.messageType](data)
      } else if (data.messageType && !this.callbacks[data.messageType]) {
        this.messageQueue.push(data)
      }
    })
    this.socketTask.onClose(() => {
      console.log('WebSocket closed')
      this.socketTask = null
    })
  }

  send(payload) {
    if (this.socketTask) {
      this.socketTask.send({
        data: JSON.stringify(payload),
        complete: (result) => {
          console.log(result)
        }
      })
    }
  }

  on(event, callback) {
    // 如果已经存在相同事件的回调，先移除旧的
    if (this.callbacks[event]) {
      console.log(`移除已存在的 ${event} 事件监听器`)
    }
    this.callbacks[event] = callback
    this.messageQueue = this.messageQueue.filter((msg) => {
      if (msg.messageType === event) {
        callback(msg)
        return false
      }
      return true
    })
  }

  off(event) {
    if (this.callbacks[event]) {
      delete this.callbacks[event]
      console.log(`移除 ${event} 事件监听器`)
    }
  }

  close() {
    if (this.socketTask) {
      this.socketTask.close()
      this.socketTask = null
    }
  }
}

const wsUrl = 'ws://192.168.110.204:8080/ws'
const wsService = new WebSocketService(wsUrl)
export default wsService 