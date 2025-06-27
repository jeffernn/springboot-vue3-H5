<template>
	<view class="login-page">
		<view class="top-section">
			<view class="app-title">欢迎登录</view>
		</view>
		
		<view class="form-container">
			<image class="logo" src="/static/1.png"></image>
			<u-form :model="form" class="login-form">
				<view class="form-item">
					<u-input v-model="form.mobile" placeholder="请输入手机号" clearable prefixIcon="phone" type="number"
						border="none" class="input-field" />
				</view>
				<view class="form-item">
					<u-input v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="请输入密码" clearable
						prefixIcon="lock" border="none" class="input-field" :suffixIcon="showPwd ? 'eye' : 'eye-off'"
						@suffix-icon-click="togglePwd" />
				</view>

				<view>
					<button class="login-btn" @click="onLogin" :loading="loading">
						<text v-if="loading">登录中...</text>
						<text v-else>登 录</text>
					</button>
				</view>

				<view class="action-row">
					<view @click="onForget"><text class="action-text">忘记密码?</text></view>
					<view @click="onRegister"><text class="action-text primary">注册账号</text></view>
				</view>
			</u-form>
		</view>
		<up-toast ref="uToastRef"></up-toast>
	</view>
</template>

<script setup lang="ts">
	import { ref } from 'vue'
	import { loginApi } from '../../http/user';
	import { onShow } from '@dcloudio/uni-app'
	import WebSocketService from '@/pages/ws/websocket.js'

	interface LoginForm {
		mobile : string
		password : string
	}

	const form = ref<LoginForm>({
		mobile: '',
		password: ''
	})

	const uToastRef = ref(null)
	function showToast(params : any) {
		uToastRef.value.show({
			...params,
			complete() {

			}
		});
	}

	const showPwd = ref(false)
	const loading = ref(false)

	// 判断是否已登录
	const checkLogin = () => {
		const token = uni.getStorageSync('accessToken')
		if (token) {
			uni.reLaunch({
				url: '/pages/index/index'
			})
		}
	}

	onShow(() => {
		checkLogin()
	})

	const onLogin = () => {
		if (!form.value.mobile) {
			showToast({
				"message": '请输入手机号'
			})
			return
		}
		if (!form.value.password) {
			showToast({
				"message": '请输入密码'
			})
			return
		}
		
		loading.value = true;
		
		loginApi(form.value.mobile, form.value.password).then(data => {
			uni.setStorageSync('accessToken', data.accessToken)
			uni.setStorageSync('tokenType', data.tokenType)
			uni.setStorageSync('loginInfo', {
				username: data.username,
				password: form.value.password
			})
			WebSocketService.send({
				userId: data.username,
				messageType: 'Student_Auth'
			});
			console.log('认证消息已发送:', {
				userId: data.username,
				messageType: 'Student_Auth'
			});
			// 安全发送student_login消息
			setTimeout(() => {
				WebSocketService.send({
					userId: data.username,
					messageType: 'student_login',
					title: '学生登录',
					content: data.username + ' 登录了系统'
				});
				console.log('H5端已发送student_login消息');
			}, 500);
			uni.reLaunch({
				url: '/pages/index/index'
			})
		}).catch((error) => {
			showToast({
				"message": '登录失败，请检查用户名和密码'
			})
		}).finally(() => {
			loading.value = false;
		})
	}

	const onForget = () => {
		showToast({
			"message": '请联系管理员找回密码'
		})
	}

	const onRegister = () => {
		showToast({
			"message": '请联系管理员申请'
		})
	}

	const togglePwd = () => {
		showPwd.value = !showPwd.value
	}
</script>

<style lang="scss" scoped>
	.login-page {
		min-height: 100vh;
		width: 100vw;
		background: linear-gradient(135deg, #7A7ACC 0%, #9683A3 100%);
		display: flex;
		flex-direction: column;
	}

	.top-section {
		height: 18vh;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		color: #fff;
		text-align: center;
	}

	.logo {
		position: absolute;
		top: -40px;
		left: 50%;
		transform: translateX(-50%);
		width: 80px;
		height: 80px;
		border-radius: 24px;
		box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
	}

	.app-title {
		font-size: 32px;
		font-weight: 700;
		text-shadow: 0 2px 4px rgba(0,0,0,0.1);
		margin-bottom: 12px;
	}
	
	.app-subtitle {
		font-size: 16px;
		color: rgba(255, 255, 255, 0.8);
		font-weight: 400;
	}

	.form-container {
		flex: 1;
		position: relative;
		background: #fff;
		border-top-left-radius: 32px;
		border-top-right-radius: 32px;
		padding: 60px 30px 40px;
		box-shadow: 0 -10px 40px rgba(0, 0, 0, 0.1);
	}

	.login-form {
		width: 100%;
	}

	.form-item {
		margin-bottom: 24px;
	}

	.input-field {
		background-color: #F2F5FC;
		border-radius: 16px;
		height: 56px;
		padding: 0 20px !important;
		border: 1px solid transparent !important;
		transition: background-color 0.3s, border-color 0.3s;
		
		::v-deep .u-input__input {
			font-size: 16px;
			color: #333;
		}

		::v-deep .u-input__placeholder {
			color: #999 !important;
		}

		::v-deep .u-icon__icon {
			color: #999 !important;
		}
	}
	
	.login-btn {
		width: 100%;
		height: 52px;
		font-size: 18px;
		font-weight: 500;
		letter-spacing: 2px;
		border-radius: 16px;
		background: linear-gradient(135deg, #7A7ACC 0%, #9683A3 100%);
		color: white;
		box-shadow: 0 8px 20px rgba(122, 122, 204, 0.3);
		border: none;
		margin-top: 16px;

		&::after {
			border: none;
		}

		&:active {
			transform: scale(0.98);
			opacity: 0.9;
		}
	}

	.action-row {
		display: flex;
		justify-content: space-between;
		margin-top: 24px;
		width: 100%;
	}

	.action-text {
		font-size: 14px;
		color: #8A8A8E;
		
		&.primary {
			color: #7A7ACC;
			font-weight: 500;
		}
	}
</style>