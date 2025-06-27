<template>
  <el-card>
    <h4>学生积分排行榜</h4>
    <el-table :data="rankList" border stripe style="width: 100%">
      <el-table-column prop="studentName" label="姓名"></el-table-column>
      <el-table-column prop="totalPoint" label="积分"></el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
defineProps({
  rankList: {
    type: Array,
    default: () => []
  }
})
</script>
