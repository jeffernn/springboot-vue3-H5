<template>
  <el-container style="margin: 20px;">
    <el-main>

      <!-- 汇总卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="summary-item">
              <h4>总学生人数</h4>
              <p>{{ summary.totalStudents }} 人</p>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover">
            <div class="summary-item">
              <h4>总积分</h4>
              <p>{{ summary.totalPoints }} 分</p>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover">
            <div class="summary-item">
              <h4>平均积分</h4>
              <p>{{ summary.avgPoints }} 分</p>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover">
            <div class="summary-item">
              <h4>总活动参与数</h4>
              <p>{{ summary.totalActivities }} 次</p>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <!-- 活动状态饼图 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="clearfix">
              <span>活动状态分布</span>
            </div>
            <div v-if="statusDistribution.length === 0" style="text-align: center; padding: 30px; color: #999;">暂无数据</div>
            <PieChart
              v-else
              :data="statusDistribution"
              chartId="activityStatusChart"
              title="活动状态分布"
            />
          </el-card>
        </el-col>

        <!-- 积分趋势柱状图 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="clearfix">
              <span>积分趋势图</span>
            </div>
            <div v-if="trendLabels.length === 0" style="text-align: center; padding: 30px; color: #999;">暂无趋势数据</div>
            <BarChart
              v-else
              :labels="trendLabels"
              :values="trendValues"
              chartId="pointTrendChart"
              title="每月积分趋势"
            />
          </el-card>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <el-row :gutter="20">
        <!-- 学生积分明细 -->
        <el-col :span="12">
          <el-card shadow="hover" style="margin-bottom: 20px;">
            <div slot="header" class="clearfix">
              <span>学生积分明细</span>
            </div>
            <el-table :data="scoreData" border stripe style="width: 100%">
              <el-table-column prop="studentName" label="学生姓名"></el-table-column>
              <el-table-column prop="totalPoint" label="累计积分"></el-table-column>
              <el-table-column prop="createTime" label="创建时间"></el-table-column>
              <el-table-column prop="updateTime" label="更新时间"></el-table-column>
            </el-table>

            <!-- 分页器 -->
            <el-pagination
              layout="prev, pager, next"
              :total="scoreTotal"
              :page-size="scoreQuery.pageSize"
              v-model:current-page="scoreQuery.pageNum"
              @current-change="handleScorePageChange"
              style="margin-top: 15px; justify-content: flex-end;"
            />
          </el-card>
        </el-col>

        <!-- 学生活动参与明细 -->
        <el-col :span="12">
          <el-card shadow="hover" style="margin-bottom: 20px;">
            <div slot="header" class="clearfix">
              <span>学生活动参与明细</span>
            </div>
            <el-table :data="activityData" border stripe style="width: 100%">
              <el-table-column prop="studentName" label="学生姓名"></el-table-column>
              <el-table-column prop="activityName" label="活动名称"></el-table-column>
              <el-table-column prop="point" label="获得积分"></el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag v-if="row.status === 2" type="success">已完成</el-tag>
                  <el-tag v-else-if="row.status === 1" type="info">申请中</el-tag>
                  <el-tag v-else type="danger">未完成</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="打卡时间"></el-table-column>
            </el-table>

            <!-- 分页器 -->
            <el-pagination
              layout="prev, pager, next"
              :total="activityTotal"
              :page-size="activityQuery.pageSize"
              v-model:current-page="activityQuery.pageNum"
              @current-change="handleActivityPageChange"
              style="margin-top: 15px; justify-content: flex-end;"
            />
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import studentScoreApi from '@/api/studentScore'
import studentActivityApi from '@/api/studentActivity'
import BarChart from '@/components/Charts/BarChart.vue'
import PieChart from '@/components/Charts/PieChart.vue'

// ========== 查询参数 ==========
const filter = ref({
  timeRange: [],
  className: ''
})

// ========== 学生积分数据 ==========
const scoreData = ref([])
const scoreTotal = ref(0)
const scoreQuery = ref({
  pageNum: 1,
  pageSize: 5
})

// ========== 学生活动数据 ==========
const activityData = ref([])
const activityTotal = ref(0)
const activityQuery = ref({
  pageNum: 1,
  pageSize: 10
})

// ========== 统计数据 ==========
const summary = computed(() => {
  const totalStudents = scoreData.value?.length || 0
  const totalPoints = scoreData.value.reduce((sum, item) => sum + (item.totalPoint || 0), 0)
  const avgPoints = totalStudents ? (totalPoints / totalStudents).toFixed(2) : 0
  const totalActivities = activityData.value?.length || 0
  return {
    totalStudents,
    totalPoints,
    avgPoints,
    totalActivities
  }
})

// ========== 活动状态分布（饼图）==========
const statusDistribution = ref([])

// ========== 积分趋势图（柱状图）==========
const trendLabels = ref([])
const trendValues = ref([])

// 获取学生积分数据
const fetchStudentScore = async () => {
  try {
    const res = await studentScoreApi.queryPage({
      ...scoreQuery.value,
      ...filter.value
    })
    if (res && Array.isArray(res.records)) {
      scoreData.value = res.records
      scoreTotal.value = res.total || res.records.length
    } else {
      scoreData.value = []
      scoreTotal.value = 0
    }
  } catch (err) {
    console.error('获取学生积分失败', err)
    scoreData.value = []
    scoreTotal.value = 0
  }
}

// 获取学生活动数据
const fetchStudentActivity = async () => {
  try {
    const res = await studentActivityApi.queryHistory({
      ...activityQuery.value,
      ...filter.value
    })
    if (res && Array.isArray(res.records)) {
      activityData.value = res.records
      activityTotal.value = res.total || res.records.length
    } else {
      activityData.value = []
      activityTotal.value = 0
    }
  } catch (err) {
    console.error('获取学生活动失败', err)
    activityData.value = []
    activityTotal.value = 0
  }
}

// 获取趋势图数据（真实调用）
const fetchTrendData = async () => {
  try {
    const res = await studentScoreApi.getMonthlyPointTrend()
    trendLabels.value = res.map(item => item.month)
    trendValues.value = res.map(item => item.total)
  } catch (err) {
    console.error('获取趋势数据失败', err)
    trendLabels.value = []
    trendValues.value = []
  }
}

// 获取活动状态分布
const fetchStatusDistribution = async () => {
  try {
    const res = await studentActivityApi.getActivityStatusStatistics()
    statusDistribution.value = Object.entries(res).map(([name, value]) => ({ name, value }))
  } catch (err) {
    console.error('获取状态分布失败', err)
    statusDistribution.value = []
  }
}

// 重新加载所有数据
const fetchData = () => {
  fetchStudentScore()
  fetchStudentActivity()
  fetchTrendData()
  fetchStatusDistribution()
}

// 重置筛选条件
const resetFilter = () => {
  filter.value = {
    timeRange: [],
    className: ''
  }
  fetchData()
}

// 分页切换 - 学生积分
const handleScorePageChange = (pageNum) => {
  scoreQuery.value.pageNum = pageNum
  fetchStudentScore()
}

// 分页切换 - 学生活动
const handleActivityPageChange = (pageNum) => {
  activityQuery.value.pageNum = pageNum
  fetchStudentActivity()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.clearfix::after {
  content: '';
  display: table;
  clear: both;
}

.summary-item {
  text-align: center;
  padding: 15px 0;
}

.summary-item h4 {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
}

.summary-item p {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.el-card__header {
  background-color: #f5f7fa;
  font-weight: bold;
}
</style>
