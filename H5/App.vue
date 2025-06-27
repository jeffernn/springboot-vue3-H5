<script>
import wsService from '@/pages/ws/websocket.js'
export default {
	onLaunch: function() {
		console.log('App Launch')
		// 检查登录状态
		this.checkLoginStatus()

		// 全局WebSocket消息监听（只注册一次）
		if (!wsService._globalListenerRegistered) {
			console.log('注册全局WebSocket监听')
			
			// 先清除可能存在的旧监听器
			wsService.off('new_activity')
			wsService.off('gift_exchange')
			wsService.off('gift_send')
			
			// 将showMsg函数绑定到wsService实例上，避免重复创建
			wsService._showMsg = (msg) => {
				console.log('全局弹窗消息:', msg)
				if (this.$refs && this.$refs.uToast) {
					this.$refs.uToast.show({
						message: msg.title || msg.content || '收到新消息',
						type: 'default',
						duration: 3000
					})
				} else {
					alert(msg.title || msg.content || '收到新消息')
				}
			}
			
			wsService.on('new_activity', wsService._showMsg)
			wsService.on('gift_exchange', wsService._showMsg)
			wsService.on('gift_send', wsService._showMsg)
			wsService._globalListenerRegistered = true
		}
	},
	onShow: function() {
		console.log('App Show')
		// 每次显示应用时检查登录状态
		this.checkLoginStatus()
	},
	onHide: function() {
		console.log('App Hide')
	},
	methods: {
		checkLoginStatus() {
			const token = uni.getStorageSync('accessToken')
			const currentPages = getCurrentPages()
			const currentPage = currentPages[currentPages.length - 1]
			// 如果当前不在登录页面且没有token，则跳转到登录页面
			if (!token && currentPage && currentPage.route !== 'pages/login/login') {
				uni.reLaunch({
					url: '/pages/login/login'
				})
			}
		}
	}
}
</script>

<template>
	<u-toast ref="uToast"></u-toast>
</template>

<style lang="scss">
	/*每个页面公共css */
	@import '@/uni_modules/uview-plus/index.scss';
	@import '@/static/apple-style.scss';
	
	page {
		font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Helvetica, Arial, sans-serif;
		color: #1D1D1F;
		background-color: #F5F5F7;
		font-size: 16px;
		line-height: 1.5;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
	}
	
	/* 统一按钮动画效果 */
	.u-button {
		transition: all 0.2s cubic-bezier(0.25, 0.46, 0.45, 0.94);
	}
	
	.u-button:active {
		transform: scale(0.98);
	}
</style>
