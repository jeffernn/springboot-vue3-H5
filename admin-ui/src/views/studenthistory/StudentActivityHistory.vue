<template>
  <el-container style="margin: 20px;">
    <el-main>
      <el-card>
        <!-- 查询表单 -->
        <el-form :inline="true" @submit.prevent="search">
          <el-form-item label="学生ID">
            <el-input v-model="queryParams.studentId" placeholder="请输入学生ID"></el-input>
          </el-form-item>
          <el-form-item label="活动ID">
            <el-input v-model="queryParams.activityId" placeholder="请输入活动ID"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getPage">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 表格 -->
        <el-table :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="id" label="记录ID" width="80"></el-table-column>
          <el-table-column prop="studentId" label="学生ID" width="100"></el-table-column>
          <el-table-column prop="studentName" label="学生姓名" width="150"></el-table-column>
          <el-table-column prop="activityId" label="活动ID" width="150"></el-table-column>
          <el-table-column prop="activityName" label="活动名称" width="200"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 2" type="success">已完成</el-tag>
              <el-tag v-else-if="row.status === 1" type="info">申请中</el-tag>
              <el-tag v-else type="danger">未完成</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="point" label="获得积分" width="200"></el-table-column>
          <el-table-column prop="createTime" label="打卡时间" width="160"></el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>

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

    <!-- 编辑抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="编辑学生活动记录"
      direction="rtl"
      size="40%"
    >
      <el-form ref="formRef" :model="currentRecord" label-width="120px" label-position="right">
        <el-form-item label="学生ID">
          <el-input v-model.number="currentRecord.studentId" disabled />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="currentRecord.studentName" disabled />
        </el-form-item>
        <el-form-item label="活动名称">
          <el-input v-model="currentRecord.activityName" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="currentRecord.status" placeholder="请选择状态">
            <el-option label="申请中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="未完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分值">
          <el-input-number v-model="currentRecord.point" :min="0" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="drawerVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import studentActivityApi from '@/api/studentActivity'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const total = ref(0)

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  studentId: null,
  activityId: null
})

// 当前编辑的记录
const currentRecord = ref({})
const drawerVisible = ref(false)

// 获取分页数据
const getPage = async () => {
  try {
    const res = await studentActivityApi.queryHistory(queryParams.value)
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

// 重置查询条件
const resetQuery = () => {
  queryParams.value.studentId = null
  queryParams.value.activityId = null
  getPage()
}

// 显示编辑弹窗
const handleEdit = (row) => {
  currentRecord.value = JSON.parse(JSON.stringify(row))
  drawerVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    await studentActivityApi.updateStudentActivity(currentRecord.value)
    ElMessage.success('更新成功')
    drawerVisible.value = false
    getPage()
  } catch (err) {
    console.error(err)
    ElMessage.error('更新失败，请检查输入或网络')
  }
}

// 删除记录
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await studentActivityApi.deleteStudentActivity({ id: row.id });
    ElMessage.success('删除成功');
    getPage(); // 刷新列表
  } catch (err) {
    console.error(err);
    ElMessage.error('删除失败，请检查网络或数据');
  }
};



// 搜索
const search = () => {
  queryParams.value.pageNum = 1
  getPage()
}

// 页面加载时获取数据
onMounted(() => {
  getPage()
})
</script>

<style scoped>
.el-table {
  margin-bottom: 20px;
}
</style>
