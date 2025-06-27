<template>
  <el-card>
    <div ref="chart" style="width: 100%; height: 400px;"></div>
  </el-card>
</template>

<script setup>
import * as echarts from 'echarts'
import { onMounted, ref, watch } from 'vue'

const props = defineProps({
  chartData: {
    type: Object,
    required: true
  }
})

const chart = ref(null)

let instance = null

const initChart = () => {
  if (!instance) {
    instance = echarts.init(chart.value)
  }

  instance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['报名人数', '打卡人数']
    },
    xAxis: {
      type: 'category',
      data: props.chartData.labels || []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '报名人数',
        type: 'bar',
        data: props.chartData.joinData || [],
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '打卡人数',
        type: 'bar',
        data: props.chartData.checkinData || [],
        itemStyle: { color: '#F56C6C' }
      }
    ]
  })
}

onMounted(() => {
  initChart()
})

watch(() => props.chartData, () => {
  initChart()
}, { deep: true })
</script>
