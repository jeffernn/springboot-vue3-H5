<template>
  <el-container style="margin: 20px;">
    <el-main>
      <el-card>
        <!-- 查询表单 -->
        <el-form :inline="true" @submit.prevent="getPage">
          <el-form-item label="学生名称">
            <el-input v-model="queryParams.studentName" placeholder="请输入学生名称"></el-input>
          </el-form-item>
          <el-form-item label="礼物名称">
            <el-input v-model="queryParams.giftName" placeholder="请输入礼物名称"></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="请选择状态">
              <el-option label="全部" :value="''" />
              <el-option label="成功" :value="1" />
              <el-option label="取消" :value="2" />
              <el-option label="待发放" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getPage">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 表格 -->
        <el-table :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="studentName" label="学生名称" width="100"></el-table-column>
          <el-table-column prop="giftName" label="礼物名称" width="100"></el-table-column>
          <el-table-column label="礼物图片">
            <template #default="{ row }">
              <el-image
                v-if="row.imageUrl"
                :src="getImageUrl(row.imageUrl)"
                :preview-src-list="['http://192.168.110.204:8080' + row.imageUrl]"
                :initial-index="0"
                fit="cover"
                style="width: 50px; height: 50px; cursor: pointer;"
              />
              <span v-else>无图片</span>
            </template>
          </el-table-column>

          <el-table-column prop="point" label="消耗积分" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              {{ statusMap[row.status] }}
            </template>
          </el-table-column>
          <el-table-column prop="receiveName" label="收件人" width="100"></el-table-column>
          <el-table-column prop="receivePhone" label="联系电话"></el-table-column>
          <el-table-column prop="address" label="地址"></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="markAsDelivered(row)">已送达</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          :page-size="queryParams.pageSize"
          v-model:current-page="queryParams.pageNum"
          @current-change="getPage"
        />
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import giftApi from '@/api/tgift'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getImageUrl} from "@/utils/imageUtils.js";

const tableData = ref([])
const total = ref(0)

const queryParams = ref({
  studentName: '',
  giftName: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})

const statusMap = {
  1: '成功',
  2: '取消',
  3: '待发放'
}

// 获取分页数据
const getPage = async () => {
  try {
    const res = await giftApi.getPage(queryParams.value)
    if (res && res.records) {
      tableData.value = res.records
      total.value = res.total || res.records.length
    } else {
      ElMessage.warning('暂无数据')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('获取数据失败，请检查网络或服务')
  }
}

// 标记为“已送达” - 修改状态为 1
const markAsDelivered = async (row) => {
  try {
    const success = await giftApi.updateStatus({ id: row.id, status: 1 })
    if (success) {
      ElMessage.success('标记为已送达')
      getPage()
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

// 删除记录
const handleDelete = async (row) => {
  ElMessageBox.confirm(`确认删除 "${row.giftName}" 的兑换记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const success = await giftApi.deleteGift({ id: row.id })
    if (success) {
      ElMessage.success('删除成功')
      getPage()
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}
// 重置查询条件
const resetQuery = () => {
  queryParams.value = {
    studentName: '',
    giftName: '',
    status: null,
    pageNum: 1,
    pageSize: 10
  }
  getPage()
}

// 初始化加载数据
getPage()
</script>
