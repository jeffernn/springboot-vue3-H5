import { ElNotification } from 'element-plus'

export function useWebSocket(url) {
  let ws = null;
  const callbacks = {};
  let isOpen = false;
  let pendingMsg = null;

  function send(msg) {
    if (ws && ws.readyState === 1) {
      ws.send(msg);
    } else {
      // 连接未建立时，暂存消息，onopen后自动发送
      pendingMsg = msg;
    }
  }

  function on(messageType, callback) {
    callbacks[messageType] = callback;
  }

  ws = new WebSocket(url);

  ws.onopen = () => {
    isOpen = true;
    console.log('WebSocket opened')
    // 连接建立后自动发送认证消息
    ws.send(JSON.stringify({
      userId: 'admin',
      messageType: 'PC_Auth'
    }));
    console.log('admin-ui端认证消息已发送');
    if (pendingMsg) {
      ws.send(pendingMsg);
      pendingMsg = null;
    }
  };

  ws.onmessage = (e) => {
    console.log('收到WebSocket消息:', e.data)
    const msg = JSON.parse(e.data);
    if (callbacks[msg.messageType]) {
      callbacks[msg.messageType](msg);
    }
    // 右上角通知，所有类型都弹窗
    if (
      msg.messageType === 'gift_exchange' ||
      msg.messageType === 'activity_card' ||
      msg.messageType === 'gift_send' ||
      msg.messageType === 'new_activity' ||
      msg.messageType === 'student_login'
    ) {
      ElNotification({
        title: msg.title || (msg.messageType === 'student_login' ? '学生登录' : ''),
        message: msg.content || '',
        type: msg.messageType === 'gift_send' ? 'success' : 'info',
        duration: 3000
      });
    }
  };

  ws.onclose = () => {
    console.log('WebSocket closed')
  };

  return { on, send };
} 