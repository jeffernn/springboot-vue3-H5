<template>
  <view class="gift-page">
    <view class="header-section">
      <view class="nav-title">积分好礼</view>
      <view class="nav-subtitle">用积分兑生活好物</view>
    </view>
    
    <view class="content-body">
      <view class="gift-content">
        <view class="total-points">
          <view class="stats-item">
            <view class="points-value">{{ userPoints !== null ? userPoints : 0 }}</view>
            <view class="points-label">当前积分</view>
          </view>
          <view class="stats-divider"></view>
          <view class="stats-item">
            <view class="points-value">{{ giftList.length }}</view>
            <view class="points-label">可选礼品</view>
          </view>
        </view>
        
        <view class="gift-list">
          <view
            v-for="(gift, index) in giftList"
            :key="gift.id"
            class="gift-card"
            :style="{ animationDelay: index * 0.05 + 's' }"
            @click="goToDetail(gift.id)"
          >
            <view class="gift-image">
              <image
                class="gift-img"
                :src="getImageUrl(gift.imageUrl)"
                mode="aspectFill"
              ></image>
              <view class="stock-badge">
                库存: {{ gift.stock || 0 }}
              </view>
            </view>
            <view class="gift-info">
              <text class="gift-title">{{ gift.name }}</text>
              <view class="gift-point-container">
                <view class="gift-point">
                  <text class="point-value">{{ gift.point }}</text>
                  <text class="point-label">积分</text>
                </view>
                <view class="exchange-btn">
                  <text>兑换</text>
                </view>
              </view>
            </view>
          </view>
        </view>
        
        <view v-if="noMore" class="no-more">没有更多礼品</view>
        <u-empty
          v-if="giftList.length === 0 && !loading"
          text="暂无礼品"
          mode="data"
        ></u-empty>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import WebSocketService from '@/pages/ws/websocket.js'

const giftList = ref([]); // 初始为空数组
const noMore = ref(false);
const loading = ref(true);
const userPoints = ref(0); // 用户积分
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

// 图片URL处理方法
const getImageUrl = (imageUrl) => {
  if (!imageUrl) return '';
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl;
  }
  return imageUrl; // 直接返回相对路径
};

// 跳转到详情页
const goToDetail = (id) => {
  console.log('尝试跳转到详情页，接收到的ID：', id);
  const url = `/pages/gift/detail?id=${id}`;
  console.log('生成的跳转URL：', url);
  uni.navigateTo({
    url: url
  });
};

// 获取用户积分
const fetchUserPoints = () => {
  const token = uni.getStorageSync('accessToken');
  const tokenType = uni.getStorageSync('tokenType');
  
  if (!token) {
    console.log('用户未登录，积分默认为0');
    return;
  }
  
  uni.request({
    url: '/api/student-scores',
    method: 'GET',
    header: {
      'Authorization': `${tokenType} ${token}`
    },
    success: (scoreRes) => {
      console.log('积分API返回数据:', JSON.stringify(scoreRes.data));
      
      // 检查返回数据类型
      const responseData = scoreRes.data;
      
      // 如果直接返回的是数组
      if (Array.isArray(responseData) && responseData.length > 0) {
        userPoints.value = responseData[0].totalPoint || 0;
        console.log('获取到的积分：', userPoints.value);
        return;
      }
      
      // 如果返回的是包含code和data的对象
      if (responseData.code === 0 && responseData.data) {
        if (Array.isArray(responseData.data) && responseData.data.length > 0) {
          userPoints.value = responseData.data[0].totalPoint || 0;
        } else if (responseData.data.totalPoint !== undefined) {
          userPoints.value = responseData.data.totalPoint;
        }
        console.log('获取到的积分：', userPoints.value);
      } else {
        console.error('无法从响应中获取积分数据');
      }
    },
    fail: (err) => {
      console.error('积分请求失败:', err);
    }
  });
};

const fetchGiftsAndPoints = () => {
  fetchUserPoints();
  // 获取礼品列表
  uni.request({
    url: '/api/admin/gift',
    method: 'GET',
    header: {
      'Authorization': `${uni.getStorageSync('tokenType')} ${uni.getStorageSync('accessToken')}`
    },
    success: (res) => {
      loading.value = false;
      if (res.data.code === 0) {
        giftList.value = res.data.data;
        if (giftList.value.length === 0) {
          noMore.value = true;
        }
      } else {
        uni.showToast({
          title: res.data.msg || '获取数据失败',
          icon: 'none'
        });
      }
    },
    fail: () => {
      loading.value = false;
      uni.showToast({
        title: '网络错误',
        icon: 'none'
      });
    }
  });
};

onMounted(() => {
    checkLogin()
    loading.value = true;
    fetchGiftsAndPoints();
    timer = setInterval(fetchGiftsAndPoints, 5000);
});

onShow(() => {
    checkLogin()
})

onUnmounted(() => {
    if (timer) clearInterval(timer);
});

WebSocketService.on('gift_exchange', (data) => {
    uNotifyRef.value.show({
        top: 10,
        type: 'warning',
        message: data.title,
        duration: 3000,
        fontSize: 20,
        safeAreaInsetTop: true
    });
})

WebSocketService.on('gift_send', (data) => {
    uNotifyRef.value.show({
        top: 10,
        type: 'success',
        message: data.title,
        duration: 3000,
        fontSize: 20,
        safeAreaInsetTop: true
    });
})
</script>

<style scoped lang="scss">
@import '@/static/apple-variables.scss';

.gift-page {
  min-height: 100vh;
  background-color: #F2F5FC;
}

.header-section {
  padding: 16px 20px 80px;
  background: linear-gradient(135deg, #7A7ACC 0%, #9683A3 100%);
  
  .nav-title {
    font-size: 28px;
    font-weight: 700;
    color: #fff;
    margin-bottom: 4px;
    text-align: center;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .nav-subtitle {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    font-weight: 400;
    text-align: center;
  }
}

.content-body {
	background-color: #F2F5FC;
	border-top-left-radius: 24px;
	border-top-right-radius: 24px;
	margin-top: -40px;
	padding: 20px;
	position: relative;
	z-index: 10;
}

.gift-content {
  padding: 0;
}

.total-points {
  margin-top: -45px;
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
  justify-content: space-around;

  .stats-item {
    text-align: center;
    flex: 1;
  }
  
  .points-label {
    font-size: 14px;
    color: #666;
    font-weight: 500;
  }
  
  .points-value {
    font-size: 32px;
    font-weight: 700;
    color: #7A7ACC;
    margin-bottom: 4px;
  }

  .stats-divider {
    width: 1px;
    height: 40px;
    background: rgba(0, 0, 0, 0.1);
  }
}

.gift-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.gift-card {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.97);
  }
}

.gift-image {
  width: 100%;
  padding-top: 100%; /* 1:1 Aspect Ratio */
  position: relative;
  
  .gift-img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .stock-badge {
    position: absolute;
    top: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: #fff;
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 0 16px 0 8px;
    z-index: 1;
  }
}

.gift-info {
  padding: 12px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.gift-title {
  font-size: 15px;
  font-weight: 500;
  color: $uni-text-color;
  line-height: 1.4;
  height: 2.8em; /* 2 lines */
  overflow: hidden;
}

.gift-point-container {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: 12px;
}

.gift-point {
  .point-value {
    font-size: 20px;
    font-weight: 600;
    color: $apple-warning;
    margin-right: 2px;
  }
  .point-label {
    font-size: 12px;
    color: $apple-warning;
  }
}

.exchange-btn {
  background: linear-gradient(135deg, rgba(122, 122, 204, 0.75) 0%, rgba(150, 131, 163, 0.75) 100%);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  border-radius: 12px;
  padding: 6px 23px;
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 2px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.no-more {
  width: 100%;
  text-align: center;
  padding: 24px 0;
  color: $uni-text-color-grey;
  font-size: 14px;
  margin-top: 16px;
  opacity: 0.7;
}
</style> 