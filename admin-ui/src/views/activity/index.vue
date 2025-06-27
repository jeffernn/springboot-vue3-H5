<template>
  <!-- No changes to template section -->
</template>

<script setup>
import { useWebSocket } from '@/utils/useWebSocket.js'
import { ElNotification } from 'element-plus'

const { on } = useWebSocket('ws://localhost:8080/ws')
on('new_activity', (data) => {
    ElNotification({
        title: data.title,
        type: 'info',
        duration: 3000
    })
    // 刷新活动列表
    fetchActivityList && fetchActivityList()
})
</script>

<style>
  /* No changes to style section */
</style>
