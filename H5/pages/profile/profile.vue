<template>
  <view class="profile-page">
    <view class="header-section">
      <view class="nav-title">个人中心</view>
    </view>

    <view class="content-section">
      <view class="user-info-card">
        <view class="user-info-left">
          <image class="avatar-img" :src="userInfo.avatar" mode="aspectFill"></image>
          <view class="user-details">
            <text class="user-name">当前用户：{{ userInfo.name || '未登录' }}</text>
          </view>
        </view>
        <button class="logout-btn" @click="logout">退出登录</button>
      </view>

      <!-- 选项卡 -->
      <view class="tab-bar">
        <view 
          :class="['tab', activeTab === 0 ? 'active' : '']" 
          @click="activeTab = 0"
        >礼品兑换记录</view>
        <view 
          :class="['tab', activeTab === 1 ? 'active' : '']" 
          @click="activeTab = 1"
        >活动参加历史</view>
      </view>

      <!-- 列表内容 -->
      <view class="list-content">
        <!-- 兑换礼物列表 -->
        <view v-if="activeTab === 0" class="tab-pane">
          <view v-for="(gift, index) in giftList" 
            :key="gift.id" 
            class="list-item gift-item"
            :style="{ animationDelay: index * 0.05 + 's' }"
          >
            <view class="gift-header">
              <image class="item-image" :src="getImageUrl(gift.imageUrl)" mode="aspectFill"></image>
              <view class="gift-basic-info">
                <text class="item-title">{{ gift.giftName || gift.name }}</text>
                <view class="points-info">
                  <u-icon name="star-fill" color="#FF9500" size="14"></u-icon>
                  <text class="points-text">{{ gift.point }} 积分</text>
                </view>
                <view class="status-badge" :class="getGiftStatusClass(gift.status)">{{ getGiftStatusText(gift.status) }}</view>
              </view>
            </view>
            
            <view class="gift-details" v-if="gift.createTime || gift.receiveName || gift.receivePhone || gift.address">
              <view class="detail-section" v-if="gift.createTime">
                <view class="detail-item">
                  <u-icon name="clock" color="#8A8A8E" size="12"></u-icon>
                  <text class="detail-label">兑换时间：</text>
                  <text class="detail-value">{{ formatTime(gift.createTime) }}</text>
                </view>
              </view>
              
              <view class="detail-section" v-if="gift.receiveName || gift.receivePhone || gift.address">
                <view class="section-title">
                  <u-icon name="account" color="#8A8A8E" size="18"></u-icon>
                  <text>收货信息</text>
                </view>
                <view class="detail-item" v-if="gift.receiveName">
                  <text class="detail-label">收货人：</text>
                  <text class="detail-value">{{ gift.receiveName }}</text>
                </view>
                <view class="detail-item" v-if="gift.receivePhone">
                  <text class="detail-label">联系电话：</text>
                  <text class="detail-value">{{ gift.receivePhone }}</text>
                </view>
                <view class="detail-item" v-if="gift.address">
                  <text class="detail-label">收货地址：</text>
                  <text class="detail-value address-text">{{ gift.address }}</text>
                </view>
              </view>
            </view>
          </view>
          <u-empty v-if="giftList.length === 0" text="暂无兑换记录" mode="data"></u-empty>
        </view>

        <!-- 参加活动列表 -->
        <view v-if="activeTab === 1" class="tab-pane">
          <view 
            v-for="(activity, index) in activityList" 
            :key="activity.id" 
            class="list-item activity-item"
            :style="{ animationDelay: index * 0.05 + 's' }"
          >
            <view class="activity-header">
              <image class="item-image" :src="getImageUrl(activity.poster)" mode="aspectFill"></image>
              <view class="activity-basic-info">
                <text class="item-title">{{ activity.title || activity.activityName }}</text>
                <view class="points-info">
                  <u-icon name="star-fill" color="#FF9500" size="14"></u-icon>
                  <text class="points-text">获得 {{ activity.point }} 积分</text>
                </view>
                <view class="status-badge" :class="getActivityStatusClass(activity.status)">{{ getActivityStatusText(activity.status) }}</view>
              </view>
            </view>
            
            <view class="activity-details" v-if="activity.startTime || activity.endTime || activity.description">
              <view class="detail-section" v-if="activity.startTime || activity.endTime">
                <view class="section-title">
                  <u-icon name="calendar" color="#8A8A8E" size="12"></u-icon>
                  <text>活动时间</text>
                </view>
                <view class="detail-item" v-if="activity.startTime">
                  <text class="detail-label">开始时间</text>
                  <text class="detail-value">{{ formatTime(activity.startTime) }}</text>
                </view>
                <view class="detail-item" v-if="activity.endTime">
                  <text class="detail-label">结束时间</text>
                  <text class="detail-value">{{ formatTime(activity.endTime) }}</text>
                </view>
              </view>
              
              <view class="detail-section" v-if="activity.description">
                <view class="section-title">
                  <u-icon name="file-text" color="#8A8A8E" size="12"></u-icon>
                  <text>活动描述</text>
                </view>
                <view class="description-content">
                  <text class="detail-value">{{ activity.description }}</text>
                </view>
              </view>
            </view>
          </view>
          <u-empty v-if="activityList.length === 0" text="暂无参与活动" mode="data"></u-empty>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';

const userInfo = ref({
  avatar: '/static/1.png', // 固定头像
  name: ''
});

const activeTab = ref(0);

const giftList = ref([]); // 兑换礼物列表
const activityList = ref([]); // 参加活动列表

let timer = null;

// 检查登录状态
const checkLogin = () => {
    const token = uni.getStorageSync('accessToken')
    if (!token) {
        uni.reLaunch({
            url: '/pages/login/login'
        })
    }
}

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

// 时间格式化函数
const formatTime = (timeStr) => {
    if (!timeStr) return '';
    try {
        const date = new Date(timeStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
    } catch (error) {
        return timeStr;
    }
};

const getGiftStatusText = (status) => {
  switch (status) {
    case 1: return '已发货';
    case 2: return '已取消';
    case 3: return '待发放';
    default: return '未知状态';
  }
};

const getGiftStatusClass = (status) => {
  switch (status) {
    case 1: return 'status-success';
    case 2: return 'status-error';
    case 3: return 'status-primary';
    default: return '';
  }
};

const getActivityStatusText = (status) => {
  switch (status) {
    case 1: return '申请中';
    case 2: return '已完成';
    case 3: return '未完成';
    default: return '未知状态';
  }
};

const getActivityStatusClass = (status) => {
  switch (status) {
    case 1: return 'status-primary';
    case 2: return 'status-success';
    case 3: return 'status-error';
    default: return '';
  }
};

// 退出登录方法
const logout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        // 清除所有存储数据
        uni.clearStorageSync();
        
        // 清除特定的存储项
        const storageKeys = [
          'loginInfo',
          'sessionId',
          'token',
          'userInfo',
          'session',
          'accessToken',
          'tokenType'
        ];
        
        storageKeys.forEach(key => {
          uni.removeStorageSync(key);
        });
        
        uni.showToast({ 
          title: '已退出', 
          icon: 'none',
          duration: 1500,
          success: () => {
            // 跳转到登录页面
            uni.reLaunch({
              url: '/pages/login/login'
            });
          }
        });
      }
    }
  });
};

const fetchProfileData = () => {
  // 获取用户名字
  const loginInfo = uni.getStorageSync('loginInfo');
  uni.request({
    url: '/api/student/login',
    method: 'POST',
    data: {
      username: loginInfo?.username || '',
      password: loginInfo?.password || ''
    },
    success: (res) => {
      if (res.data.code === 0) {
        userInfo.value.name = res.data.data.name || res.data.data.username || '';
        if (!userInfo.value.avatar) {
          userInfo.value.avatar = '/static/1.png';
        }
      }
    }
  });
  // 获取兑换礼品列表
  uni.request({
    url: '/api/admin/gift/exchange/list',
    method: 'GET',
    header: {
      'Authorization': `${uni.getStorageSync('tokenType')} ${uni.getStorageSync('accessToken')}`
    },
    success: (res) => {
      if (res.data.code === 0) {
        giftList.value = res.data.data || [];
      }
    }
  });
  // 获取活动参与列表
  uni.request({
    url: '/api/student-activity-history',
    method: 'GET',
    header: {
      'Authorization': `${uni.getStorageSync('tokenType')} ${uni.getStorageSync('accessToken')}`
    },
    success: (res) => {
      if (res.data.code === 0) {
        activityList.value = res.data.data || [];
      }
    }
  });
};

onMounted(() => {
    checkLogin()
    fetchProfileData();
    timer = setInterval(fetchProfileData, 5000);
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

.profile-page {
  min-height: 100vh;
  background-color: #F2F5FC;
}

.header-section {
  padding: 16px 20px 80px;
  background: linear-gradient(135deg, #7A7ACC 0%, #9683A3 100%);
  text-align: center;
}

.header-section .nav-title {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-section .nav-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 400;
}

.content-section {
  background-color: transparent;
  border-top-left-radius: 24px;
  border-top-right-radius: 24px;
  margin-top: -95px;
  padding: 20px;
  position: relative;
  z-index: 10;
}

.user-info-card {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar-img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #555;
}

.logout-btn {
  width: auto;
  background: linear-gradient(135deg, rgba(122, 122, 204, 0.75) 0%, rgba(150, 131, 163, 0.75) 100%);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: #fff !important;
  border: 1px solid rgba(255, 255, 255, 0.2);
  height: 36px;
  border-radius: 18px;
  font-size: 13px;
  font-weight: 500;
  padding: 0 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  &:after {
    border: none;
  }
}

.tab-bar {
  display: flex;
  background-color: rgba(122, 122, 204, 0.1);
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 24px;
  
  .tab {
    flex: 1;
    text-align: center;
    padding: 10px 0;
    font-size: 16px;
    font-weight: 500;
    color: #666;
    border-radius: 10px;
    transition: all 0.3s ease;
    
    &.active {
      color: #7A7ACC;
      font-weight: 600;
      background-color: #fff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }
  }
}

.list-content {
  .list-item {
    background: rgba(255, 255, 255, 0.5);
    border: 1px solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border-radius: 16px;
    margin-bottom: 16px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    }
  }
  
  .gift-header {
    display: flex;
    padding: 16px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  }
  
  .item-image {
    width: 80px;
    height: 80px;
    border-radius: 12px;
    object-fit: cover;
    margin-right: 16px;
    flex-shrink: 0;
  }
  
  .gift-basic-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    position: relative;
  }
  
  .item-title {
    font-size: 16px;
    font-weight: 600;
    color: $uni-text-color;
    margin-bottom: 8px;
    display: block;
    line-height: 1.4;
  }
  
  .points-info {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 8px;
  }
  
  .points-text {
    font-size: 14px;
    color: $apple-warning;
    font-weight: 500;
  }
  
  .status-badge {
    position: absolute;
    top: 0;
    right: 0;
    padding: 4px 8px;
    border-radius: 8px;
    font-size: 11px;
    font-weight: 500;

    &.status-primary {
      background-color: rgba(122, 122, 204, 0.1);
      color: #7A7ACC;
    }
    &.status-success {
      background-color: rgba(52, 199, 89, 0.1);
      color: $apple-success;
    }
    &.status-error {
      background-color: rgba(255, 59, 48, 0.1);
      color: $apple-error;
    }
  }
  
  .gift-details {
    padding: 16px;
    background-color: rgba(255, 255, 255, 0.2);
  }
  
  .detail-section {
    margin-bottom: 16px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .section-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 600;
    color: #8A8A8E;
    margin-bottom: 8px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
  
  .detail-item {
    display: flex;
    align-items: center;
    margin-bottom: 6px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .detail-label {
    font-size: 12px;
    color: #8A8A8E;
    width: 60px;
    flex-shrink: 0;
  }
  
  .detail-value {
    font-size: 12px;
    color: $uni-text-color;
    flex: 1;
    
    &.address-text {
      word-break: break-all;
      line-height: 1.4;
    }
  }
  
  // 活动历史样式
  .activity-header {
    display: flex;
    padding: 16px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  }
  
  .activity-basic-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    position: relative;
    padding-right: 70px;
  }
  
  .activity-details {
    padding: 16px;
    background-color: rgba(255, 255, 255, 0.2);
  }
  
  .description-content {
    margin-top: 8px;
    
    .detail-value {
      font-size: 12px;
      color: $uni-text-color;
      line-height: 1.5;
      word-break: break-all;
    }
  }
}

.tab-pane {
  padding: 0;
}
</style>