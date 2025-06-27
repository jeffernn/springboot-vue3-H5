<template>
	<view class="activity-page">
		<!-- 顶部导航 -->
		<view class="header-section">
			<view class="nav-bar">
				<view class="nav-title">探索活动</view>
				<view class="nav-subtitle">发现精彩，赢取积分</view>
			</view>
		</view>
		
		<view class="content-body">
			<view class="search-bar">
				<view class="search-input-wrapper" :class="{ focused: isSearchFocused, filled: searchTitle }">
					<u-icon name="search" color="#764ba2" size="20"></u-icon>
					<input
						v-model="searchTitle"
						placeholder="快来搜索吧🎉～"
						class="search-input"
						@focus="isSearchFocused = true"
						@blur="isSearchFocused = false"
						@confirm="onSearch"
					/>
				</view>
			</view>
			<view class="activities-wrapper">
				<view v-for="activity in activityList" :key="activity.id" class="activity-card" @click="goToDetail(activity.id)">
					<view class="image-container">
						<image class="activity-image" :src="getImageUrl(activity.poster)" mode="widthFix"></image>
						<view v-if="isExpired(activity.endTime)" class="status-tag expired">已结束</view>
						<view v-else-if="isOngoing(activity.startTime, activity.endTime)" class="status-tag ongoing">进行中</view>
						<view v-else class="status-tag upcoming">即将开始</view>
						<view class="points-badge">
							<u-icon name="star-fill" color="#FFFFFF" size="14"></u-icon>
							<text>{{ activity.point }}</text>
						</view>
					</view>
					<view class="activity-info">
						<text class="activity-title">{{ activity.title }}</text>
						<text v-if="activity.description" class="activity-description">{{ activity.description }}</text>
						<view class="activity-meta">
							<view class="time-info">
								<u-icon name="clock" color="#8A8A8E" size="14"></u-icon>
								<text class="time-text">{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</text>
							</view>
						</view>
						<view class="activity-actions" v-if="!isExpired(activity.endTime)">
							<button class="action-btn join-btn" @click.stop="handleAction(activity, 'join')">
								<text>立即参加</text>
							</button>
							<button class="action-btn checkin-btn" @click.stop="handleAction(activity, 'checkin')">
								<text>签到打卡</text>
							</button>
						</view>
					</view>
				</view>
			</view>
			
			<u-loadmore :status="loadStatus" @loadmore="loadMore" v-if="activityList.length > 0" />
			<u-empty v-if="activityList.length === 0 && loadStatus === 'nomore' && !hasError" text="暂无活动" mode="data"></u-empty>
			<u-empty v-if="hasError" :text="'错误信息: ' + errorMsg" mode="network"></u-empty>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import WebSocketService from '@/pages/ws/websocket.js'

const activityList = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const loadStatus = ref('loading'); // loading, nomore, more
const hasError = ref(false); // 新增：标记是否有错误
const errorMsg = ref(''); // 新增：存储错误信息
const searchTitle = ref(''); // 新增：搜索标题
const isSearchFocused = ref(false);
let timer = null;
const uNotifyRef = ref(null)

// 检查登录状态
const checkLogin = () => {
    const token = uni.getStorageSync('accessToken')
    if (!token) {
        uni.reLaunch({
            url: '/pages/login/login'
        })
    }
}

const fetchActivities = async () => {
    console.log('fetchActivities 调用，关键词：', searchTitle.value);
    if (loadStatus.value === 'nomore' && pageNum.value > 1) return; // Prevent fetching more if no more data and not initial load
    if (loadStatus.value === 'loading' && pageNum.value > 1) return; // Prevent multiple requests while loading

    loadStatus.value = 'loading';
    hasError.value = false; // 重置错误状态
    errorMsg.value = ''; // 清除错误信息

    try {
        const res = await uni.request({
            url: '/api/activity/public/listAll', // 请根据您的实际后端地址修改
            method: 'GET',
            data: {
                pageNum: pageNum.value,
                pageSize: pageSize.value,
                title: searchTitle.value // 支持搜索
            }
        });
        const responseData = res.data; // 假设res.data就是您的Result对象
        console.log('API Response Data:', responseData); // 添加这行来打印响应数据

        if (responseData.code === 0 && responseData.data) {
            if (pageNum.value === 1) {
                // 前端过滤
                if (searchTitle.value) {
                    activityList.value = responseData.data.filter(item =>
                        item.title && item.title.includes(searchTitle.value)
                    );
                } else {
                    activityList.value = responseData.data;
                }
            } else {
                activityList.value = [...activityList.value, ...responseData.data]; // 分页加载，追加
            }
            // console.log('Updated activityList:', activityList.value); // 还原：打印更新后的activityList
            if (responseData.data.length < pageSize.value) { // If fewer items than pageSize are returned, it's the last page
                loadStatus.value = 'nomore';
            } else {
                loadStatus.value = 'more';
            }
        } else {
            console.error('获取活动数据失败:', responseData.msg || '未知错误');
            loadStatus.value = 'nomore';
            hasError.value = true; // 设置错误状态
            errorMsg.value = '获取活动数据失败：' + (responseData.msg || '服务器返回未知错误'); // 设置更明确的错误信息
        }
    } catch (error) {
        console.error('请求活动数据异常:', error);
        loadStatus.value = 'nomore';
        hasError.value = true; // 设置错误状态
        errorMsg.value = '网络请求失败，请检查网络或服务器。' + (error.message || ''); // 设置更友好的错误信息，并包含原生错误信息
    }
};

const isExpired = (endTime) => {
    const now = new Date();
    // 假设endTime格式为 'YYYY-MM-DD HH:mm:ss' 或 'YYYY-MM-DD'
    const end = new Date(endTime.replace(/-/g, '/')); // 兼容Safari的日期解析
    return end < now;
};

const isOngoing = (startTime, endTime) => {
    const now = new Date();
    const start = new Date(startTime.replace(/-/g, '/'));
    const end = new Date(endTime.replace(/-/g, '/'));
    return now >= start && now <= end;
    // return true; // 临时设置为true，用于调试
};

const formatDate = (dateString) => {
    if (!dateString) return '';
    // 假设dateString是 'YYYY-MM-DD HH:mm:ss' 或 'YYYY-MM-DD'
    return dateString.split(' ')[0];
};

// 添加处理图片URL的方法
const getImageUrl = (poster) => {
    if (!poster) return '';
    // 如果已经是完整的URL，直接返回
    if (poster.startsWith('http://') || poster.startsWith('https://')) {
        return poster;
    }
    // 使用相对路径，让代理来处理
    return poster; // 直接返回相对路径，如 /upload/xxx.jpeg
};

const loadMore = () => {
    if (loadStatus.value === 'more') {
        pageNum.value++;
        fetchActivities();
    }
};

// 统一处理参加和打卡操作
const handleAction = async (activity, actionType) => {
    try {
        // 获取登录信息
        const loginInfo = uni.getStorageSync('loginInfo');
        if (!loginInfo || !loginInfo.username || !loginInfo.password) {
            uni.showToast({
                title: '请先登录',
                icon: 'none',
                duration: 2000
            });
            return;
        }

        // 构建请求数据
        const checkInData = {
            activityId: activity.id,
            activityName: activity.title,
            activityTime: `${formatDate(activity.startTime)} ~ ${formatDate(activity.endTime)}`,
            point: activity.point,
            status: actionType === 'join' ? 1 : 2, // 1表示申请中，2表示已完成
            username: loginInfo.username,
            password: loginInfo.password
        };

        // 获取token信息
        const tokenType = uni.getStorageSync('tokenType');
        const accessToken = uni.getStorageSync('accessToken');

        const res = await uni.request({
            url: '/api/activity/check-in',
            method: 'POST',
            data: checkInData,
            header: {
                'content-type': 'application/json',
                'Authorization': `${tokenType} ${accessToken}`
            }
        });

        console.log('操作响应:', res);

        if (res.data.code === 0) {
            uni.showToast({
                title: actionType === 'join' ? '参加成功' : '签到成功',
                icon: 'success',
                duration: 2000
            });
        } else {
            uni.showToast({
                title: res.data.msg || (actionType === 'join' ? '参加失败' : '签到失败'),
                icon: 'none',
                duration: 2000
            });
        }
    } catch (error) {
        console.error('请求异常:', error);
        uni.showToast({
            title: '网络错误',
            icon: 'none',
            duration: 2000
        });
    }
};

const goToDetail = (id) => {
    console.log('尝试跳转到详情页，接收到的ID：', id);
    const url = `/pages/activity/detail?id=${id}`;
    console.log('生成的跳转URL：', url);
    uni.navigateTo({
        url: url,
        fail: (err) => {
            console.error('跳转失败：', err);
            uni.showToast({
                title: '跳转失败',
                icon: 'none'
            });
        }
    });
};

// 新增：搜索方法
const onSearch = () => {
    console.log('点击了搜索，关键词：', searchTitle.value);
    pageNum.value = 1;
    fetchActivities();
};

onMounted(() => {
    checkLogin()
    fetchActivities();
    timer = setInterval(fetchActivities, 5000); // 每5秒轮询
});

onShow(() => {
    checkLogin()
})

onUnmounted(() => {
    if (timer) clearInterval(timer);
});
</script>

<style scoped lang="scss">
@import '@/static/apple-variables.scss';

.activity-page {
    min-height: 100vh;
    background-color: #F2F5FC;
}

/* 顶部导航区域 */
.header-section {
  padding: 16px 20px 60px;
  background: linear-gradient(135deg, #7A7ACC 0%, #9683A3 100%);
  .nav-bar {
    text-align: center;
    .nav-title {
      font-size: 28px;
      font-weight: 700;
      color: #fff;
      margin-bottom: 4px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    .nav-subtitle {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.8);
      font-weight: 400;
    }
  }
}

.search-bar {
  padding: 0 4px;
  margin-bottom: 20px;
  margin-top: -38px;
  .search-input-wrapper {
    display: flex;
    align-items: center;
    flex: 1;
    background: rgba(255, 255, 255, 0.25);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border-radius: 24px;
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.12),
      0 4px 16px rgba(118, 75, 162, 0.08),
      inset 0 1px 0 rgba(255, 255, 255, 0.4),
      inset 0 -1px 0 rgba(255, 255, 255, 0.1);
    padding: 0 14px;
    height: 48px;
    border: 1px solid rgba(255, 255, 255, 0.4);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(
        135deg,
        rgba(255, 255, 255, 0.1) 0%,
        rgba(255, 255, 255, 0.05) 50%,
        rgba(255, 255, 255, 0.1) 100%
      );
      border-radius: 24px;
      pointer-events: none;
    }

    .u-icon {
      margin-right: 8px;
      position: relative;
      z-index: 2;
    }

    .search-input {
      flex: 1;
      border: none;
      outline: none;
      background: transparent;
      font-size: 17px;
      color: #333;
      height: 48px;
      line-height: 48px;
      position: relative;
      z-index: 2;
      &::placeholder {
        color: #6a5a8a;
        opacity: 0.7;
        font-weight: 400;
      }
    }

    &.focused {
      background: rgba(255, 255, 255, 0.35);
      backdrop-filter: blur(25px) saturate(200%);
      -webkit-backdrop-filter: blur(25px) saturate(200%);
      border-color: rgba(118, 75, 162, 0.6);
      box-shadow: 
        0 12px 40px rgba(118, 75, 162, 0.2),
        0 8px 24px rgba(0, 0, 0, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.6),
        inset 0 -1px 0 rgba(255, 255, 255, 0.2);
      transform: scale(1.02) translateY(-1px);
      
      &::before {
        background: linear-gradient(
          135deg,
          rgba(118, 75, 162, 0.1) 0%,
          rgba(255, 255, 255, 0.15) 50%,
          rgba(118, 75, 162, 0.1) 100%
        );
      }
    }
    
    &.filled:not(.focused) {
      background: rgba(255, 255, 255, 0.3);
      backdrop-filter: blur(22px) saturate(190%);
      -webkit-backdrop-filter: blur(22px) saturate(190%);
      border-color: rgba(118, 75, 162, 0.4);
      box-shadow: 
        0 6px 24px rgba(118, 75, 162, 0.12),
        0 4px 16px rgba(0, 0, 0, 0.1),
        inset 0 1px 0 rgba(255, 255, 255, 0.5),
        inset 0 -1px 0 rgba(255, 255, 255, 0.15);
    }
  }
}

.content-body {
	background-color: #F2F5FC;
	border-top-left-radius: 24px;
	border-top-right-radius: 24px;
	margin-top: -30px;
	padding: 20px;
	position: relative;
	z-index: 10;
}

.activities-wrapper {
    padding: 0;
}

.activity-card {
    background: rgba(255, 255, 255, 0.5);
    border: 1px solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border-radius: 20px;
    margin-bottom: 20px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    
    &:active {
        transform: scale(0.98);
    }

    .image-container {
        position: relative;
        width: 100%;
        height: auto;
        overflow: hidden;
        background: transparent;

        .activity-image {
            width: 100%;
            height: auto;
            // object-fit: cover; // 保持注释
        }

        .status-tag {
            position: absolute;
            top: 12px;
            left: 12px;
            z-index: 10;
            color: #fff;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
            background: rgba(0, 0, 0, 0.3);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
            
            &.expired { background: rgba(80, 80, 80, 0.5); }
            &.ongoing { background: rgba(117, 142, 214, 0.65); }
            &.upcoming { background: rgba(255, 149, 0, 0.6); }
        }

        .points-badge {
            position: absolute;
            top: 0;
            right: 0;
            background-color: rgba(255, 149, 0, 0.5);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            color: #fff;
            font-size: 14px;
            font-weight: 600;
            padding: 4px 12px;
            border-radius: 0 0 0 12px;
            z-index: 1;
            display: flex;
            align-items: center;
            gap: 3px;
            border-left: 1px solid rgba(255, 255, 255, 0.2);
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
        }
    }

    .activity-info {
        padding: 20px;

        .activity-title {
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 12px;
            color: $uni-text-color;
            display: block;
        }

        .activity-description {
            font-size: 14px;
            color: $uni-text-color-grey;
            margin-bottom: 16px;
            line-height: 1.5;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
        }

        .activity-meta {
            display: flex;
            flex-direction: column;
            margin-bottom: 12px;
            color: #8A8A8E;
            font-size: 14px;

            .meta-row {
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 16px;
            }

            .time-info {
                display: flex;
                align-items: center;
                gap: 6px;
                flex: 1;
            }

            .time-text {
                font-size: 14px;
                color: #666;
                font-weight: 500;
            }
        }

        .activity-actions {
            margin-top: 20px;
            display: flex;
            gap: 12px;
            
            .action-btn {
                flex: 1;
                display: flex;
                align-items: center;
                justify-content: center;
                height: 44px;
                padding: 0 16px;
                border-radius: 12px;
                font-size: 16px;
                font-weight: 500;
                transition: all 0.2s;
                border: none;
                
                &:after {
                    border: none;
                }
                
                &:active {
                    transform: scale(0.96);
                    opacity: 0.9;
                }
            }
            
            .join-btn {
                background: linear-gradient(135deg, rgba(122, 122, 204, 0.75) 0%, rgba(150, 131, 163, 0.75) 100%);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
                border: 1px solid rgba(255, 255, 255, 0.2);
                color: white;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            }
            
            .checkin-btn {
                background: linear-gradient(135deg, rgba(122, 122, 204, 0.5) 0%, rgba(150, 131, 163, 0.3) 100%);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
                color: white;
                border: 1px solid rgba(255, 255, 255, 0.2);
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            }
        }
    }
}
</style>
