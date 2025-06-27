<template>
  <view class="detail-page">
    <view class="header-nav" :class="{ 'is-scrolled': isScrolled }">
      <view class="nav-back" @click="goBack">
        <u-icon name="arrow-left" :color="isScrolled ? '#333' : '#fff'" size="22"></u-icon>
      </view>
      <view class="nav-title" :class="{ 'is-scrolled': isScrolled }">礼品详情</view>
    </view>
    
    <view v-if="gift" class="gift-content">
      <view class="gift-image-wrapper">
        <image class="gift-image" :src="getImageUrl(gift.imageUrl)" mode="aspectFill"></image>
      </view>
      
      <view class="info-card">
        <view class="point-section">
          <text class="gift-name">{{ gift.name }}</text>
          <view class="point-right">
            <text class="point-value">{{ gift.point }}</text>
            <text class="point-label">积分</text>
          </view>
        </view>

        <view class="description-section">
          <text class="section-title">礼品描述</text>
          <text class="description-content">{{ gift.description || '暂无详细描述' }}</text>
        </view>
        
        <view class="receiver-info">
          <text class="section-title">收货信息</text>
          <view class="input-group">
            <input class="textarea-field" v-model="receiverInfo.name" placeholder="收货人姓名" />
          </view>
          <view class="input-group">
            <input class="textarea-field" v-model="receiverInfo.phone" placeholder="联系电话" type="number" />
          </view>
          <view class="input-group">
            <textarea class="textarea-field" v-model="receiverInfo.address" placeholder="详细收货地址" />
          </view>
        </view>
        
        <view class="action-section">
          <button class="action-btn" @click="handleExchange">立即兑换</button>
        </view>
      </view>
    </view>
    
    <u-empty v-if="!gift" text="礼品详情加载失败或不存在" mode="data"></u-empty>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onLoad, onShow, onPageScroll } from '@dcloudio/uni-app';

const gift = ref(null);
const gift_id = ref(null);
const receiverInfo = ref({
  name: '',
  phone: '',
  address: ''
});
const isExchanging = ref(false);
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
const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  // 假设这是一个相对路径，需要拼接完整的URL
  return `${url}`;
};

const goBack = () => {
  uni.navigateBack();
};

const handleExchange = async () => {
  // 验证表单
  if (!receiverInfo.value.name) {
    uni.showToast({
      title: '请输入收货人姓名',
      icon: 'none'
    });
    return;
  }
  if (!receiverInfo.value.phone) {
    uni.showToast({
      title: '请输入联系电话',
      icon: 'none'
    });
    return;
  }
  if (!receiverInfo.value.address) {
    uni.showToast({
      title: '请输入收货地址',
      icon: 'none'
    });
    return;
  }

  // 开始loading
  isExchanging.value = true;
  uni.showLoading({
    title: '正在兑换中...'
  });

  try {
    const res = await uni.request({
      url: '/api/admin/gift/check-stock-and-exchange',
      method: 'POST',
      data: {
        id: gift.value.id,
        status: 3,
        receiveName: receiverInfo.value.name,
        receivePhone: receiverInfo.value.phone,
        address: receiverInfo.value.address
      },
      header: {
        'Authorization': `${uni.getStorageSync('tokenType')} ${uni.getStorageSync('accessToken')}`
      }
    });

    if (res.data.code === 0) {
      uni.showToast({
        title: '兑换成功',
        icon: 'success'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    } else if (res.data.code === 400) {
      uni.showToast({
        title: '当前积分不足无法兑换',
        icon: 'none'
      });
    } 
	else if (res.data.code === 460) {
	  uni.showToast({
	    title: '当前库存不足无法兑换',
	    icon: 'none'
	  });
	} else {
      uni.showToast({
        title: res.data.msg || '兑换失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('兑换请求失败:', error);
    uni.showToast({
      title: '网络错误',
      icon: 'none'
    });
  } finally {
    // 结束loading
    isExchanging.value = false;
    uni.hideLoading();
  }
};

onLoad((options) => {
  gift_id.value = options.id;
  console.log('Loaded gift detail page with gift_ID:', gift_id.value);
});

onMounted(() => {
  checkLogin()
  if (gift_id.value) {
    uni.request({
      url: `/api/admin/gift`,
      method: 'GET',
      header: {
        'Authorization': `${uni.getStorageSync('tokenType')} ${uni.getStorageSync('accessToken')}`
      },
      success: (res) => {
        console.log('API Response:', res.data);
        if (res.data.code === 0 && res.data.data) {
          // 从返回的数组中找到匹配的礼物
          const foundGift = res.data.data.find(item => item.id === parseInt(gift_id.value));
          if (foundGift) {
            gift.value = foundGift;
            // 如果已有收货信息，填充表单
            if (foundGift.receive_name) {
              receiverInfo.value.name = foundGift.receive_name;
            }
            if (foundGift.receive_phone) {
              receiverInfo.value.phone = foundGift.receive_phone;
            }
            if (foundGift.address) {
              receiverInfo.value.address = foundGift.address;
            }
          } else {
            uni.showToast({
              title: '未找到该礼品',
              icon: 'none'
            });
          }
        } else {
          uni.showToast({
            title: res.data.msg || '获取礼品详情失败',
            icon: 'none'
          });
        }
      },
      fail: () => {
        uni.showToast({
          title: '网络错误，无法获取礼品详情',
          icon: 'none'
        });
      }
    });
  }
});

onShow(() => {
    checkLogin()
})
</script>

<style scoped lang="scss">
@import '@/static/apple-variables.scss';

.detail-page {
  background: linear-gradient(135deg, #F2F5FC 0%, #E6E9F4 100%);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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

.gift-content {
  padding-top: var(--status-bar-height);
  width: 100%;
  background-color: transparent;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.gift-image-wrapper {
  width: 100%;
  height: 600rpx;
  position: relative;
  
  .gift-image {
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

.point-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);

  .gift-name {
    font-size: 20px;
    font-weight: 600;
    color: $uni-text-color;
    margin-right: 8px;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .point-right {
    display: flex;
    align-items: baseline;
    gap: 4px;
  }

  .point-value {
    font-size: 28px;
    font-weight: 700;
    color: $apple-warning;
    margin-right: 0;
  }

  .point-label {
    font-size: 16px;
    color: #8A8A8E;
  }
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  display: block;
  color: $uni-text-color;
}

.description-section {
  margin-bottom: 32px;
  
  .description-content {
    font-size: 16px;
    line-height: 1.7;
    color: #666;
  }
}

.receiver-info {
  .input-group {
    margin-bottom: 16px;
  }

  .textarea-field {
    width: 100%;
    background: rgba(255, 255, 255, 0.4);
    border: 1px solid rgba(0, 0, 0, 0.05);
    border-radius: 12px;
    padding: 16px;
    font-size: 16px;
    box-sizing: border-box;
    color: #333;
    line-height: 1.6;
    
    &::placeholder {
      color: #6a5a8a;
      opacity: 0.7;
    }
  }

  input.textarea-field {
    height: 52px;
  }

  textarea.textarea-field {
    height: 120px;
  }
}

.action-section {
  margin-top: 32px;
  
  .action-btn {
    width: 100%;
    height: 50px;
    border-radius: 12px;
    font-size: 18px;
    font-weight: 500;
    background: linear-gradient(135deg, #7A7ACC 0%, #9683A3 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(122, 122, 204, 0.3);
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;

    &:active {
      transform: scale(0.98);
      opacity: 0.9;
    }
    
    &::after {
      border: none;
    }
  }
}
</style> 