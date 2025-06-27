# 通知需求

## 通知模式

websocket方式，

通知：

系统通知：操作系统即，可以随时通知，骚扰通知

app通知：前提：app在线

通知就是数据：系统通知就有预定义的格式，app通知，数据格式自己定义，一般都是借鉴他人优秀成果。

## 通知类型

新活动发布

当后台新增一个活动后，则向H5端所有在线的所有学生发布通知，并在顶部显示title，同时刷新活动页面

礼物兑换

当学生兑换一个礼物后，则向B端的管理者发送兑换消息，并且在右上角显示title，与content

成功打卡

当学生成功打卡后，则向B端的管理者发送兑换消息，并且在右上角显示title

礼物已送达

当后台成功将一个礼物送达后，则向H5端对应的学生发布通知，并在顶部显示title

B端认证

链接的时候，不需要身份认证，用户登录之后在进行身份认证，获取对方UserId

当成功连接服务器后，向服务器发送当前id

H5端认证

当成功连接服务器后，向服务器发送当前id





## 消息实体设计模型

```java
public class MessageV0 {
    private Integer userId; // 接收者
    private String title; // 消息标题
    private String content; // 消息内容 
    private String messageType; // 通知类型
    private String param; // 参数字符串
}
```



## B端认证 实体例子

```json
{
    userId:1,
    messageType:'PC_Auth'
}
```

## H5端认证 实体例子

```json
{
    userId:1,
    messageType:'Student_Auth'
}
```

## 新活动发布 实体例子 

## 前端发送：有可能发送，新增接口成功之后，可能被用户关闭浏览器

## 后端发送：一致性，成功发送，失败不会发送

```json
{
    userId:1,
    title:'xxx活动已发布'
    messageType:'new_activity',
}
```

## 礼物兑换 实体例子

```json
{
    userId:1,
    title:'xxx兑换yyy',
    messageType:'gift_exchange',
    content:'请即使发货',
}
```

## 成功打卡 实体例子

```json
{
    userId:1,
    title:'ikun成功打卡ikun舞会',
    messageType:'activity_card',
}
```

## 礼物已送达 实体

```json
{
    userId:1,
    title:'您的ikun手办已送达'
    messageType:'gift_send',
}
```



## springboot

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
-----------------------------------------------------------  
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyTextWebSocketHandler(), "/ws").setAllowedOriginPatterns("*");
    }
}
------------------------------------------------------------
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyTextWebSocketHandler extends TextWebSocketHandler {
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("收到消息: " + payload);

        // 回复一条文本消息
        session.sendMessage(new TextMessage("服务端已收到: " + payload));
    }
}
```



## vue3

useWebSocket.js

```javascript
/**
 * @author lwlianghehe@gmail.com
 * @date 2025/6/23
 */

import { ref, onMounted, onBeforeUnmount } from "vue";

/**
 * 打开链接 ws://localhost:8080/ws vue函数组件
 * @param url WebSocket地址
 */
export function useWebSocket(url: string) {
  let ws: any = null;
  const callbacks: any = {};

  /**
   * 发送
   * @param msg 字符串
   */
  function send(msg: any) {
    if (ws && ws.readyState === 1) {
      ws.send(msg);
    }
  }

  function on(messageType: string, callback: any) {
    callbacks[messageType] = callback;
  }

  // 表示在组件使用
  onMounted(() => {
    ws = new WebSocket(url);
    // websocket 接收消息之后的处理事件
    ws.onmessage = (e: any) => {
      const msg = JSON.parse(e.data);
      if (callbacks[msg.messageType]) {
        callbacks[msg.messageType](msg);
      }
    };
  });

  onBeforeUnmount(() => {
    ws && ws.close();
  });

  return { on, send };
}

```

使用：

```
<script setup>
import { useWebSocket } from './useWebSocket.js';
const { messages, send } = useWebSocket('ws://localhost:8080/ws');

import { getCurrentInstance } from "vue";

const instance = getCurrentInstance();
const globalProperty =
  instance?.appContext.config.globalProperties;
</script>

```

## h5端

websocket.js

```javascript
class WebSocketService {
	constructor(url) {
		this.url = url
		this.socketTask = null
		this.callbacks = {}
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
		})
		this.socketTask.onMessage((res) => {
			const data = JSON.parse(res.data)
			// 触发对应的回调
			if (data.messageType && this.callbacks[data.messageType]) {
				this.callbacks[data.messageType](data)
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
		this.callbacks[event] = callback
	}

	close() {
		if (this.socketTask) {
			this.socketTask.close()
			this.socketTask = null
		}
	}
}

export default new WebSocketService('ws://localhost:8080/ws')
```

main.js

```json
import WebSocketService from './pages/ws/websocket.js'
WebSocketService.connect()
app.config.globalProperties.$ws = WebSocketService
```



使用

login

```javascript
let app = getApp();
if (app) {
    let msg = {
        userId: data.id,
        messageType: 'Student_Auth'
    }
    app.$ws.send(msg)
}
```

activity

```json
<up-notify ref="uNotifyRef" :message="message"></up-notify>

let app = getApp();
const message = ref('')
const uNotifyRef = ref(null)

app.$ws.on('new_activity', (data) => {
        uNotifyRef.value.show({
            top: 10,
            type: 'primary',
            message: data.title,
            duration: 1000 * 3,
            fontSize: 20,
            safeAreaInsetTop: true
        });
        setTimeout(() => {
            pageNum.value = 1
            activityList.value.splice(0, activityList.value.length)
            fetchActivityList()
        }, 1000)
    })
```



## 二维码打卡

需求：让参加活动的学生打卡，每一个活动都需要生成对应的二维码，二维码就是个url

1、准备二维码。二维码 等于url，url的要求：生成活动签到页面 http://localhost:5173/#/pages/qr_card/qr_card?activity=1

2、扫码显示签到页面 实现页面，接口 获取活动，不能被拦截

3、点击签到按钮  需要登录 点击签到接口，用之前