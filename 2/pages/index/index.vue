<template>
	<view class="main-page">
		<view class="tab-content apple-anim-fade">
			<activityVue v-if="activeTab === 0" />
			<scoringVue v-if="activeTab === 1" />
			<giftVue v-if="activeTab === 2" />
			<profileVue v-if="activeTab === 3" />
		</view>
		
		<!-- 苹果风格底部导航栏 -->
		<view class="apple-tabbar glass apple-safe-area">
			<view 
				class="apple-tabbar-item" 
				:class="{ active: activeTab === 0 }"
				@click="onTabChange(0)"
			>
				<u-icon name="calendar" :color="activeTab === 0 ? '#7566c9' : '#8E8E93'" size="22"></u-icon>
				<text class="apple-tabbar-item-text">当前活动</text>
			</view>
			<view 
				class="apple-tabbar-item" 
				:class="{ active: activeTab === 1 }"
				@click="onTabChange(1)"
			>
				<u-icon name="heart" :color="activeTab === 1 ? '#7566c9' : '#8E8E93'" size="22"></u-icon>
				<text class="apple-tabbar-item-text">排行榜</text>
			</view>
			<view 
				class="apple-tabbar-item" 
				:class="{ active: activeTab === 2 }"
				@click="onTabChange(2)"
			>
				<u-icon name="gift" :color="activeTab === 2 ? '#7566c9' : '#8E8E93'" size="22"></u-icon>
				<text class="apple-tabbar-item-text">积分好礼</text>
			</view>
			<view 
				class="apple-tabbar-item" 
				:class="{ active: activeTab === 3 }"
				@click="onTabChange(3)"
			>
				<u-icon name="account" :color="activeTab === 3 ? '#7566c9' : '#8E8E93'" size="22"></u-icon>
				<text class="apple-tabbar-item-text">个人信息</text>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref, onMounted } from 'vue'
	import { onShow } from '@dcloudio/uni-app'
	import activityVue from '../activity/activity.vue'
	import scoringVue from '../scoring/scoring.vue'
	import giftVue from '../gift/gift.vue'
	import profileVue from '../profile/profile.vue'

	const activeTab = ref(0)
	
	// 检查登录状态
	const checkLogin = () => {
		const token = uni.getStorageSync('accessToken')
		if (!token) {
			uni.reLaunch({
				url: '/pages/login/login'
			})
		}
	}
	
	onMounted(() => {
		checkLogin()
	})
	
	onShow(() => {
		checkLogin()
	})
	
	const onTabChange = (index : number) => {
		activeTab.value = index
	}
</script>

<style scoped lang="scss">
	@import '@/static/apple-variables.scss';
	
	.main-page {
		min-height: 100vh;
		background: #F2F5FC;
		display: flex;
		flex-direction: column;
		justify-content: flex-start;
	}

	.tab-content {
		flex: 1;
		padding-bottom: 90rpx;
	}

	.apple-tabbar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		justify-content: space-around;
		height: 60px;
		background: rgba(255, 255, 255, 0.8);
		backdrop-filter: blur(20px);
		-webkit-backdrop-filter: blur(20px);
		border-top: 1px solid rgba(0, 0, 0, 0.05);
		padding-bottom: env(safe-area-inset-bottom);
		
		.apple-tabbar-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			flex: 1;
			gap: 2px;
			transition: all 0.2s ease;

			.apple-tabbar-item-text {
				font-size: 10px;
				font-weight: 500;
				color: #8E8E93;
			}

			&.active {
				.apple-tabbar-item-text {
					color: #7566c9;
				}
				
				.u-icon {
					transform: scale(1.1);
				}
			}
		}
	}
</style>