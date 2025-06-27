// 初始化 WebSocket 连接
const ws = new WebSocket('ws://localhost:8080/ws')
ws.onopen = () => {
  console.log('WebSocket 已连接')
}
ws.onclose = () => {
  console.log('WebSocket 已关闭')
}
ws.onerror = (err) => {
  console.error('WebSocket 发生错误', err)
}
ws.onmessage = (msg) => {
  console.log('收到 WebSocket 消息:', msg.data)
}
window.ws = ws
