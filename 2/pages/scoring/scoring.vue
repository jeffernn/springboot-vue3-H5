<template>
  <view class="leaderboard-container">
    <!-- 顶部导航栏 -->
    <view class="header-section">
      <view class="nav-bar">
        <view class="nav-title">积分排行榜</view>
        <view class="nav-subtitle">实时更新学生积分排名</view>
      </view>
    </view>
    
    <!-- 主要内容区域 -->
    <view class="main-content">
      <!-- 排行榜卡片 -->
      <view class="leaderboard-card">
        <!-- 统计信息 -->
        <view class="stats-section">
          <view class="stats-item">
            <view class="stats-number">{{ leaderboardData.length }}</view>
            <view class="stats-label">参与人数</view>
          </view>
          <view class="stats-divider"></view>
          <view class="stats-item">
            <view class="stats-number">{{ maxScore }}</view>
            <view class="stats-label">最高积分</view>
          </view>
        </view>
        
        <view class="card-divider"></view>
        
        <!-- 排行榜内容 -->
        <view class="leaderboard-content">
          <view v-if="loading" class="loading-container">
            <view class="loading-spinner"></view>
            <text class="loading-text">加载排行榜数据...</text>
          </view>
          
          <view v-else-if="leaderboardData.length === 0" class="empty-container">
            <view class="empty-icon">📊</view>
            <text class="empty-text">暂无排行榜数据</text>
            <text class="empty-desc">请稍后再试</text>
          </view>
          
          <view v-else class="leaderboard-list">
            <!-- 前三名特殊展示 -->
            <view v-if="leaderboardData.length > 0" class="top-three-section">
              <view class="top-three-title">🏅🏅🏅</view>
              <view class="top-three-container">
                <!-- 第二名 -->
                <view v-if="leaderboardData[1]" class="top-item second-place">
                  <view class="top-rank">2</view>
                  <view class="top-avatar">{{ leaderboardData[1].name.charAt(0) }}</view>
                  <view class="top-name">{{ leaderboardData[1].name }}</view>
                  <view class="top-score">{{ leaderboardData[1].score }}分</view>
                </view>
                
                <!-- 第一名 -->
                <view v-if="leaderboardData[0]" class="top-item first-place">
                  <view class="crown">👑</view>
                  <view class="top-rank">1</view>
                  <view class="top-avatar">{{ leaderboardData[0].name.charAt(0) }}</view>
                  <view class="top-name">{{ leaderboardData[0].name }}</view>
                  <view class="top-score">{{ leaderboardData[0].score }}分</view>
                </view>
                
                <!-- 第三名 -->
                <view v-if="leaderboardData[2]" class="top-item third-place">
                  <view class="top-rank">3</view>
                  <view class="top-avatar">{{ leaderboardData[2].name.charAt(0) }}</view>
                  <view class="top-name">{{ leaderboardData[2].name }}</view>
                  <view class="top-score">{{ leaderboardData[2].score }}分</view>
                </view>
              </view>
            </view>
            
            <!-- 其他排名列表 -->
            <view v-if="leaderboardData.length > 3" class="other-ranks-section">
              <view class="section-title">其他排名</view>
              <view class="rank-list">
                <view 
                  v-for="(item, index) in leaderboardData.slice(3)" 
                  :key="index + 3" 
                  class="rank-item"
                >
                  <view class="rank-number">{{ index + 4 }}</view>
                  <view class="student-info">
                    <view class="student-avatar">{{ item.name.charAt(0) }}</view>
                    <view class="student-details">
                      <view class="student-name">{{ item.name }}</view>
                      <view class="score-progress">
                        <view class="progress-bar">
                          <view class="progress-fill" :style="{ width: getScoreBarWidth(item.score) }"></view>
                        </view>
                        <view class="score-text">{{ item.score }}分</view>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
            
            <!-- 列表末尾提示 -->
            <view v-if="leaderboardData.length > 0" class="end-of-list-container">
              <view class="end-of-list-text">--- 已经到底啦 ---</view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { onMounted, onUnmounted } from 'vue';

export default {
  data() {
    return {
      loading: false,
      leaderboardData: [],
      maxScore: 0,
      timer: null,
      firstLoad: true,
    };
  },
  methods: {
    // 检查登录状态
    checkLogin() {
      const token = uni.getStorageSync('accessToken')
      if (!token) {
        uni.reLaunch({
          url: '/pages/login/login'
        })
      }
    },
    
    async fetchLeaderboardData() {
      if (this.firstLoad) {
        this.loading = true;
      }
      try {
        const response = await uni.request({
          url: '/api/student-scores',
          method: 'GET',
          header: {
            'Content-Type': 'application/json'
          }
        });
        
        console.log('API响应：', response);
        
        if (response.statusCode === 200 && response.data) {
          const processedData = response.data
            .map(item => ({
              name: item.studentName,
              score: item.totalPoint
            }))
            .sort((a, b) => b.score - a.score);
          
          this.leaderboardData = processedData;
          
          if (processedData.length > 0) {
            this.maxScore = processedData[0].score;
          }
        } else {
          uni.showToast({
            title: '获取数据失败',
            icon: 'none',
            duration: 2000
          });
        }
      } catch (error) {
        console.error('获取排行榜数据失败:', error);
        uni.showToast({
          title: '网络请求失败',
          icon: 'none',
          duration: 2000
        });
      } finally {
        if (this.firstLoad) {
          this.loading = false;
          this.firstLoad = false;
        }
      }
    },
    
    getScoreBarWidth(score) {
      if (this.maxScore === 0) return '0%';
      const percent = (score / this.maxScore) * 100;
      return Math.min(percent, 100) + '%';
    }
  },
  mounted() {
    this.checkLogin()
    this.fetchLeaderboardData();
    this.timer = setInterval(this.fetchLeaderboardData, 5000);
  },
  activated() {
    this.checkLogin()
    this.fetchLeaderboardData();
  },
  unmounted() {
    if (this.timer) clearInterval(this.timer);
  }
};
</script>

<style lang="scss" scoped>
@import '@/static/apple-variables.scss';

.leaderboard-container {
  min-height: 100vh;
  background-color: #F2F5FC;
  padding-top: 0;
}

/* 顶部导航区域 */
.header-section {
  padding: 16px 20px 80px;
  background: linear-gradient(135deg, #7A7ACC 0%, #9683A3 100%);
  
  .nav-bar {
    text-align: center;
    
    .nav-title {
      font-size: 28px;
      font-weight: 700;
      color: #fff;
      margin-bottom: 8px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    
    .nav-subtitle {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.8);
      font-weight: 400;
    }
  }
}

/* 主要内容区域 */
.main-content {
  padding: 20px;
  margin-top: -85px;
  position: relative;
  z-index: 10;
}

/* 排行榜卡片 */
.leaderboard-card {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* 统计卡片 */
.stats-section {
  display: flex;
  align-items: center;
  justify-content: space-around;

  .stats-item {
    text-align: center;
    flex: 1;
    
    .stats-number {
      font-size: 32px;
      font-weight: 700;
      color: #7A7ACC;
      margin-bottom: 4px;
    }
    
    .stats-label {
      font-size: 14px;
      color: #666;
      font-weight: 500;
    }
  }
  
  .stats-divider {
    width: 1px;
    height: 40px;
    background: rgba(0, 0, 0, 0.1);
    margin: 0 20px;
  }
}

.card-divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.1);
  margin: 24px 0;
}

/* 加载状态 */
.loading-container {
  text-align: center;
  padding: 60px 0;
  
  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 3px solid rgba(122, 122, 204, 0.2);
    border-top: 3px solid #7A7ACC;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 16px;
  }
  
  .loading-text {
    font-size: 16px;
    color: #666;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 空状态 */
.empty-container {
  text-align: center;
  padding: 60px 0;
  
  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
  
  .empty-text {
    font-size: 18px;
    color: #666;
    font-weight: 500;
    display: block;
    margin-bottom: 8px;
  }
  
  .empty-desc {
    font-size: 14px;
    color: #999;
  }
}

/* 前三名区域 */
.top-three-section {
  margin-bottom: 32px;
  
  .top-three-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    text-align: center;
    margin-bottom: 20px;
  }
  
  .top-three-container {
    display: flex;
    align-items: flex-end;
    justify-content: center;
    gap: 16px;
    padding: 0 20px;
  }
  
  .top-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    transition: transform 0.3s ease;
    
    &:hover {
      transform: translateY(-4px);
    }
    
    .crown {
      position: absolute;
      top: -20px;
      font-size: 24px;
      z-index: 2;
    }
    
    .top-rank {
      font-size: 14px;
      font-weight: 600;
      color: #fff;
      background: rgba(0, 0, 0, 0.3);
      width: 24px;
      height: 24px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 8px;
    }
    
    .top-avatar {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: linear-gradient(135deg, #7A7ACC, #9683A3);
      color: #fff;
      font-size: 24px;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12px;
      box-shadow: 0 4px 16px rgba(122, 122, 204, 0.3);
    }
    
    .top-name {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 4px;
      text-align: center;
    }
    
    .top-score {
      font-size: 16px;
      font-weight: 700;
      color: #7A7ACC;
    }
  }
  
  .first-place {
    .top-avatar {
      width: 80px;
      height: 80px;
      font-size: 32px;
      background: linear-gradient(135deg, #FFD700, #FFA500);
      box-shadow: 0 6px 20px rgba(255, 215, 0, 0.4);
    }
    
    .top-score {
      color: #FFA500;
    }
  }
  
  .second-place {
    .top-avatar {
      background: linear-gradient(135deg, #C0C0C0, #A9A9A9);
      box-shadow: 0 4px 16px rgba(192, 192, 192, 0.3);
    }
    
    .top-score {
      color: #A9A9A9;
    }
  }
  
  .third-place {
    .top-avatar {
      background: linear-gradient(135deg, #CD7F32, #B8860B);
      box-shadow: 0 4px 16px rgba(205, 127, 50, 0.3);
    }
    
    .top-score {
      color: #B8860B;
    }
  }
}

/* 其他排名区域 */
.other-ranks-section {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
    padding-left: 8px;
  }
  
  .rank-list {
    .rank-item {
      display: flex;
      align-items: center;
      padding: 16px 12px;
      background: rgba(255, 255, 255, 0.6);
      border-radius: 16px;
      margin-bottom: 12px;
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.8);
        transform: translateX(4px);
      }
      
      .rank-number {
        width: 28px;
        height: 28px;
        background: #7A7ACC;
        color: #fff;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 700;
        font-size: 14px;
        margin-right: 16px;
        flex-shrink: 0;
        box-shadow: 0 2px 4px rgba(122, 122, 204, 0.3);
      }
      
      .student-info {
        flex: 1;
        display: flex;
        align-items: center;
        
        .student-avatar {
          width: 40px;
          height: 40px;
          background: linear-gradient(135deg, #7A7ACC, #9683A3);
          color: #fff;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 600;
          font-size: 16px;
          margin-right: 12px;
          flex-shrink: 0;
          box-shadow: 0 2px 8px rgba(122, 122, 204, 0.3);
        }
        
        .student-details {
          flex: 1;
          
          .student-name {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
          }
          
          .score-progress {
            display: flex;
            align-items: center;
            gap: 12px;
            
            .progress-bar {
              flex: 1;
              height: 6px;
              background: rgba(0, 0, 0, 0.1);
              border-radius: 3px;
              overflow: hidden;
              
              .progress-fill {
                height: 100%;
                background: linear-gradient(90deg, #7A7ACC, #9683A3);
                border-radius: 3px;
                transition: width 0.8s cubic-bezier(0.16, 1, 0.3, 1);
              }
            }
            
            .score-text {
              font-size: 14px;
              font-weight: 600;
              color: #7A7ACC;
              min-width: 50px;
              text-align: right;
            }
          }
        }
      }
    }
  }
}

/* 只有前三名的情况 */
.only-top-three {
  text-align: center;
  padding: 40px 0;
  
  .no-more-text {
    font-size: 16px;
    color: #666;
    font-weight: 500;
  }
}

.end-of-list-container {
  text-align: center;
  padding: 24px 0 0;
  
  .end-of-list-text {
    font-size: 14px;
    color: #999;
  }
}
</style>
