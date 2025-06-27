<template>
  <view class="detail-page">
    <view class="header-nav" :class="{ 'is-scrolled': isScrolled }">
      <view class="nav-back" @click="goBack">
        <u-icon name="arrow-left" :color="isScrolled ? '#333' : '#fff'" size="22"></u-icon>
      </view>
      <view class="nav-title" :class="{ 'is-scrolled': isScrolled }">活动详情</view>
    </view>
    
    <view v-if="activity" class="activity-content">
      <view class="activity-image-wrapper">
        <image class="activity-image" :src="getImageUrl(activity.poster)" mode="aspectFill"></image>
      </view>
      
      <view class="info-card">
        <view class="description-section title-section">
          <text class="activity-title">{{ activity.title }}</text>
        </view>

        <view class="meta-section">
          <view class="meta-item">
            <u-icon name="calendar" color="#8A8A8E" size="18"></u-icon>
            <view class="meta-content">
              <text class="meta-label">开始时间</text>
              <text class="meta-value">{{ activity.startTime }}</text>
            </view>
          </view>
          <view class="meta-item">
            <u-icon name="clock" color="#8A8A8E" size="18"></u-icon>
            <view class="meta-content">
              <text class="meta-label">结束时间</text>
              <text class="meta-value">{{ activity.endTime }}</text>
            </view>
          </view>
          <view class="meta-item">
            <u-icon name="star-fill" color="#FF9500" size="18"></u-icon>
            <view class="meta-content">
              <text class="meta-label">可获积分</text>
              <text class="meta-value points">{{ activity.point }}</text>
            </view>
          </view>
        </view>

        <view class="description-section">
          <text class="section-title">活动详情</text>
          <text class="description-content">{{ activity.description || '暂无详细描述' }}</text>
        </view>

        <view class="status-section">
          <view v-if="isExpired(activity.endTime)" class="status-tag expired">
            <u-icon name="close-circle" color="#fff" size="20"></u-icon>
            <text>活动已结束</text>
          </view>
        </view>
        
        <view class="action-section" v-if="!isExpired(activity.endTime)">
          <button class="action-btn join-btn" @click="handleAction('join')">立即参加</button>
          <button class="action-btn checkin-btn" @click="handleAction('checkin')">签到打卡</button>
        </view>
      </view>
    </view>
    <u-empty v-else text="活动详情加载失败或不存在" mode="data"></u-empty>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { onLoad, onShow, onPageScroll } from '@dcloudio/uni-app';

const activity = ref(null);
const activity_id = ref(null);
let timer = null;
const isScrolled = ref(false);

onPageScroll((e) => {
  isScrolled.value = e.scrollTop > 50;
});

// 检查登录状态
const checkLogin = () => {
    const token = uni.getStorageSync('accessToken')
    if (!token) {
        uni.reLaunch({
            url: '/pages/login/login'
        })
    }
}

// 图片URL处理方法
const getImageUrl = (poster) => {
  if (!poster) return '';
  if (poster.startsWith('http://') || poster.startsWith('https://')) {
    return poster;
  }
  return poster;
};

const isExpired = (endTime) => {
  const now = new Date();
  const end = new Date(endTime.replace(/-/g, '/'));
  return end < now;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  return dateString.split(' ')[0];
};

const goBack = () => {
  uni.navigateBack();
};

const handleAction = async (actionType) => {
  try {
    const loginInfo = uni.getStorageSync('loginInfo');
    if (!loginInfo || !loginInfo.username || !loginInfo.password) {
      uni.showToast({
        title: '请先登录',
        icon: 'none',
        duration: 2000
      });
      return;
    }

    const checkInData = {
      activityId: activity.value.id,
      activityName: activity.value.title,
      activityTime: `${formatDate(activity.value.startTime)} ~ ${formatDate(activity.value.endTime)}`,
      point: activity.value.point,
      status: actionType === 'join' ? 1 : 2,
      username: loginInfo.username,
      password: loginInfo.password
    };

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

const fetchDetail = () => {
  if (activity_id.value) {
    uni.request({
      url: '/api/activity/public/listAll',
      method: 'GET',
      success: (res) => {
        if (res.data.code === 0 && res.data.data) {
          const foundActivity = res.data.data.find(item => item.id === parseInt(activity_id.value));
          if (foundActivity) {
            activity.value = foundActivity;
          }
        }
      }
    });
  }
};

onLoad((options) => {
  activity_id.value = options.id;
});

onMounted(() => {
  checkLogin()
  fetchDetail();
  timer = setInterval(fetchDetail, 5000);
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

.detail-page {
  background: linear-gradient(135deg, #F2F5FC 0%, #E6E9F4 100%);
  min-height: 100vh;
  padding-top: 0;
}

.header-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  padding-top: var(--status-bar-height);
  z-index: 100;
  background: transparent;
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
  border-bottom: none;
  transition: background-color 0.3s, border-bottom 0.3s, backdrop-filter 0.3s;
  
  &.is-scrolled {
    background: rgba(255, 255, 255, 0.75);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border-bottom: 0.5px solid rgba(0, 0, 0, 0.1);
  }

  .nav-back {
    position: absolute;
    left: 8px;
    top: var(--status-bar-height);
    height: 44px;
    width: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    background-color: rgba(0, 0, 0, 0.25);
    transition: background-color 0.3s;
  }
  
  &.is-scrolled .nav-back {
    background-color: transparent;
    &:active {
      background-color: rgba(0, 0, 0, 0.05);
    }
  }

  .nav-title {
    font-size: 17px;
    font-weight: 600;
    color: transparent;
    transition: color 0.3s;
    &.is-scrolled {
      color: #333;
    }
  }
}

.activity-content {
  padding-top: var(--status-bar-height);
  width: 100%;
  background-color: transparent;
  display: flex;
  flex-direction: column;
}

.activity-image-wrapper {
  width: 100%;
  height: 600rpx;
  position: relative;
  
  .activity-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.info-card {
  margin: -60rpx 0 0;
  padding: 30px 20px 40px;
  background: rgba(255, 255, 255, 0.6);
  border: none;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 20px 20px 0 0;
  box-shadow: none;
  position: relative;
  z-index: 10;
  flex: 1;
}

.meta-section {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);

  .meta-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .meta-content {
    display: flex;
    flex-direction: column;
  }

  .meta-label {
    font-size: 14px;
    color: #8A8A8E;
    margin-bottom: 4px;
  }

  .meta-value {
    font-size: 16px;
    font-weight: 500;
    color: #000;
    &.points {
      color: $apple-warning;
      font-weight: 600;
    }
  }
}

@media (max-width: 500px) {
  .meta-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .meta-section .meta-item {
    width: 100%;
    justify-content: flex-start;
  }
}

.description-section {
  margin-bottom: 32px;
  
  &.title-section {
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  }

  .activity-title {
    font-size: 24px;
    font-weight: 700;
    color: $uni-text-color;
    line-height: 1.4;
  }

  .section-title {
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 12px;
    display: block;
    color: $uni-text-color;
  }
  
  .description-content {
    font-size: 16px;
    line-height: 1.7;
    color: #666;
    white-space: pre-line;
  }
}

.status-section {
  margin-bottom: 24px;
  
  .status-tag {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background: rgba(80, 80, 80, 0.5);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    color: #fff;
    padding: 12px 20px;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 500;
  }
}

.action-section {
  display: flex;
  gap: 12px;
  
  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 48px;
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
</style> 