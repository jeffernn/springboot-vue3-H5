<template>
  <el-container style="margin: 20px;">
    <el-main>
      <el-card>
        <!-- 查询表单 -->
        <el-form :inline="true" @submit.prevent="search">
          <el-form-item label="活动标题">
            <el-input v-model="queryParams.title" placeholder="请输入活动标题"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getPage">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
            <el-button type="success" @click="handleAdd">新增活动</el-button>
          </el-form-item>
        </el-form>

        <!-- 表格 -->
        <el-table :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="title" label="活动标题"></el-table-column>
          <el-table-column prop="description" label="描述"></el-table-column>
          <el-table-column prop="startTime" label="开始时间"></el-table-column>
          <el-table-column prop="endTime" label="结束时间"></el-table-column>
          <el-table-column prop="point" label="积分值"></el-table-column>
          <el-table-column label="活动海报" width="150">
            <template #default="{ row }">
              <el-image
                v-if="row.poster"
                :src="getImageUrl(row.poster)"
                :preview-src-list="[getImageUrl(row.poster)]"
                style="width: 100px; height: 100px;"
                fit="cover"
              >
                <template #error>
                  <div class="image-slot">加载失败</div>
                </template>
              </el-image>
              <span v-else>无海报</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
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

    <!-- 新增/编辑活动抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      :title="isEdit ? '编辑活动' : '新增活动'"
      direction="rtl"
      size="50%"
    >
      <el-form ref="formRef" :model="currentActivity" label-width="100px" :rules="rules" label-position="right">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="currentActivity.title" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="currentActivity.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="currentActivity.startTime"
            type="datetime"
            placeholder="请选择开始时间"
            value-format="YYYY-MM-DD HH:mm"
            format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="currentActivity.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            value-format="YYYY-MM-DD HH:mm"
            format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>

        <el-form-item label="积分数值" prop="point">
          <el-input-number v-model="currentActivity.point" :min="0" />
        </el-form-item>
        <el-form-item label="上传海报">
          <el-upload
            ref="uploadRef"
            :action="uploadUrl"
            list-type="picture-card"
            :show-file-list="true"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="uploadHeaders"
            :limit="2"
            :on-exceed="handleExceed"
            :file-list="posterFileList"
          >
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">支持JPG/PNG/GIF格式，大小不超过5MB</div>
          </el-upload>
        </el-form-item>

        <!-- 显示已上传的海报 -->
        <el-form-item label="当前海报">
          <div v-if="currentActivity.poster" class="thumbnail-container">
            <el-image
              :src="getPosterUrl(currentActivity.poster)"
              :preview-src-list="[getPosterUrl(currentActivity.poster)]"
              style="width: 150px;"
            >
              <template #error>
                <div class="image-slot">加载失败</div>
              </template>
            </el-image>
          </div>
          <div v-else>暂无海报</div>
        </el-form-item>
        <el-form-item label="规则描述" prop="ruleDescription">
          <el-input v-model="currentActivity.ruleDescription" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="drawerVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed,nextTick } from 'vue'
import activityApi from '@/api/activity'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getImageUrl} from "@/utils/imageUtils.js";

const activities = ref([])
const drawerVisible = ref(false)
const isEdit = ref(false)
const uploadRef = ref()
const posterFileList = ref([])
const queryParams = ref({
  title: '',
  pageNum: 1,
  pageSize: 10
})


const tableData = ref([])
const total = ref(0)

// 当前编辑的活动数据
const currentActivity = ref({
  id: null,
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  point: 0,
  poster: '',
  ruleDescription: ''
})

// 表单校验规则
const rules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  startTime: [{ required: true, message: '请输入开始时间', trigger: 'blur' }],
  endTime: [{ required: true, message: '请输入结束时间', trigger: 'blur' }]
}

// 获取分页数据
const getPage = async () => {
  try {
    const res = await activityApi.getPage(queryParams.value)
    if (res && res.records) {
      tableData.value = res.records
      total.value = res.total || res.records.length
    } else {
      ElMessage.warning('暂无数据')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('获取活动数据失败，请检查网络或后端服务')
  }
}

function handleAvatarSuccess(response, file) {
  // 清空原有文件列表并设置新文件
  posterFileList.value = [file]

  let url = ''
  if (typeof response === 'string') {
    url = response
  } else if (response?.data) {
    url = response.data
  } else {
    ElMessage.error('上传失败，请检查响应格式')
    return
  }

  currentActivity.value.poster = url
  ElMessage.success('上传成功')
}


const uploadUrl = computed(() => {
  return '/dev-api/api/upload/poster'
})

function beforeAvatarUpload(file) {
  const isValidType = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isValidSize = file.size / 1024 / 1024 < 5 // 小于 5MB

  if (!isValidType) {
    ElMessage.error('只能上传 JPG/PNG/GIF 格式!')
    return false
  }
  if (!isValidSize) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }


  return isValidType && isValidSize
}


function handleExceed(files, fileList) {
  ElMessage.warning('只能上传一张海报')
}

// 计算属性获取 token
const token = computed(() => {
  return localStorage.getItem('token') || ''
})

// 请求头配置
const uploadHeaders = computed(() => {
  const tokenValue = localStorage.getItem('token') || ''
  return {
    Authorization: `pc ${tokenValue}`
  }
})

// 重置查询条件
const resetQuery = () => {
  queryParams.value.title = ''
  getPage()
}

// 显示新增弹窗
const handleAdd = () => {
  isEdit.value = false
  currentActivity.value = {
    id: null,
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    point: 0,
    poster: '',
    ruleDescription: ''
  }
  drawerVisible.value = true
}

// 显示编辑弹窗
const handleEdit = (row) => {
  isEdit.value = true
  currentActivity.value = JSON.parse(JSON.stringify(row))
  drawerVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    const headers = {
      Authorization: `pc ${token.value}`
    }
    if (isEdit.value) {
      await activityApi.save(currentActivity.value, { headers })
      ElMessage.success('更新成功')
    } else {
      await activityApi.save(currentActivity.value, { headers })
      ElMessage.success('新增成功')
      // 新增：推送 websocket 消息
      if (window.ws && window.ws.readyState === 1) {
        window.ws.send(JSON.stringify({ type: 'new_activity', data: currentActivity.value }))
      }
    }
    drawerVisible.value = false
    getPage()
  } catch (err) {
    console.error(err)
    ElMessage.error('操作失败，请检查输入或网络')
  }
}

// 删除活动
const handleDelete = async (row) => {
  ElMessageBox.confirm(`确认删除活动 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await activityApi.deleteActivity({ id: row.id })
    ElMessage.success('删除成功')
    getPage()
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}




// 搜索
const search = () => {
  getPage()
}

// 页面加载时获取数据
onMounted(() => {
  getPage()
})

// 构建海报完整路径
const getPosterUrl = (posterPath) => {
  return getImageUrl(posterPath)
}

</script>

<style scoped>
.thumbnail-container {
  display: flex;
  align-items: center;
}
.image-slot {
  font-size: 12px;
  color: #999;
}
</style>
