<template>
  <!-- No changes to template section -->
</template>

<script setup>
import { useWebSocket } from '@/utils/useWebSocket.js'
import { ElNotification } from 'element-plus'

const { on } = useWebSocket('ws://localhost:8080/ws')

on('gift_exchange', (data) => {
    ElNotification({
        title: data.title,
        message: data.content || '',
        type: 'warning',
        duration: 3000
    })
})

on('gift_send', (data) => {
    ElNotification({
        title: data.title,
        type: 'success',
        duration: 3000
    })
})
</script> 