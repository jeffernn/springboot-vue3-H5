<template>
  <div :id="chartId" style="height: 300px;"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { onMounted } from 'vue'

const props = defineProps({
  data: Array,
  chartId: String,
  title: String
})

onMounted(() => {
  const chart = echarts.init(document.getElementById(props.chartId))
  chart.setOption({
    title: { text: props.title },
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: props.data,
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
      }
    }]
  })
})
</script>
