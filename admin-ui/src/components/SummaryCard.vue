<template>
  <el-card class="summary-card">
    <div class="card-content">
      <h4>{{ title }}</h4>
      <p class="count">{{ count }}</p>
    </div>
  </el-card>
</template>

<script setup>
defineProps({
  title: String,
  count: [String, Number]
})
</script>

<style scoped>
.summary-card {
  text-align: center;
}
.card-content h4 {
  font-size: 16px;
  color: #666;
}
.card-content .count {
  font-size: 24px;
  font-weight: bold;
  margin-top: 8px;
}
</style>
