<template>
  <div :id="chartId" style="height: 300px;"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { onMounted } from 'vue'

const props = defineProps({
  labels: Array,
  values: Array,
  chartId: String,
  title: String
})

onMounted(() => {
  const chart = echarts.init(document.getElementById(props.chartId))
  chart.setOption({
    title: { text: props.title },
    tooltip: {},
    xAxis: {
      type: 'category',
      data: props.labels
    },
    yAxis: { type: 'value' },
    series: [{ data: props.values, type: 'bar' }]
  })
})
</script>
