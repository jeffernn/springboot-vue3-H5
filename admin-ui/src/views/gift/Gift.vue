<template>
  <div class="gift-page">
    <el-card shadow="never">
      <!-- 搜索与操作 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-input v-model="queryParams.name" placeholder="礼物名称" clearable />
        </el-col>
        <el-col :xs="24" :sm="6" :md="4" :lg="3">
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </el-col>
        <el-col :xs="24" :sm="6" :md="4" :lg="3">
          <el-button type="success" @click="handleAdd">新增</el-button>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="name" label="礼物名称" width="150"></el-table-column>
        <el-table-column prop="point" label="所需积分" width="120"></el-table-column>
        <el-table-column prop="stock" label="库存数量" width="120"></el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip></el-table-column>
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <img :src="getImageUrl(row.imageUrl)" alt="礼物图片" style="width: 100%; max-height: 80px; object-fit: contain;" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="160"></el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <el-pagination
        layout="prev, pager, next"
        :total="total"
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        @current-change="handlePageChange"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="60%" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" label-width="120px" :rules="rules" label-position="right">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="礼物名称" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所需积分" prop="point">
              <el-input-number v-model="form.point" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="form.description" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="图片上传">
              <el-upload
                action="/dev-api/api/upload/poster"
                list-type="picture-card"
                :on-success="handleUploadSuccess"
                :limit="2"
                :on-exceed="handleExceed"
                :before-upload="beforeUpload"
                :file-list="form.imageList"
              >
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item style="text-align: right">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '@/utils/request';
import { getImageUrl} from "@/utils/imageUtils.js";

const tableData = ref([]);
const total = ref(0);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: ''
});
const dialogVisible = ref(false);
const dialogTitle = ref('新增礼物');
const form = ref({
  id: null,
  name: '',
  point: 0,
  description: '',
  imageUrl: '',
  stock: 0,
  imageList: []
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入礼物名称', trigger: 'blur' }],
  point: [{ required: true, message: '请输入所需积分', trigger: 'change' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'change' }]
};

// 获取分页数据
const fetchData = () => {
  request.post('/api/admin/gift/page', queryParams.value).then(res => {
    tableData.value = res.records;
    total.value = res.total;
  });
};

// 分页切换
const handlePageChange = (pageNum) => {
  queryParams.value.pageNum = pageNum;
  fetchData();
};

// 新增初始化
const handleAdd = () => {
  dialogTitle.value = '新增礼物';
  form.value = {
    id: null,
    name: '',
    point: 0,
    description: '',
    imageUrl: '',
    stock: 0,
    imageList: []
  };
  dialogVisible.value = true;
};

// 编辑赋值
const handleEdit = (row) => {
  dialogTitle.value = '编辑礼物';
  form.value = {
    id: row.id,
    name: row.name,
    point: row.point,
    description: row.description,
    imageUrl: row.imageUrl,
    stock: row.stock || 0,
    imageList: row.imageUrl ? [{ url: getImageUrl(row.imageUrl) }] : []
  };
  dialogVisible.value = true;
};

// 删除
const handleDelete = (id) => {
  request.post('/api/admin/gift/delete', { id }).then(() => {
    fetchData();
  });
};

// 图片上传成功回调
const handleUploadSuccess = (res, file) => {
  if (res.code === 0) {
    form.value.imageUrl = res.data;
    form.value.imageList = [file];
  }
};

// 上传限制提醒
const handleExceed = () => {
  alert('只能上传一张图片');
};

// 上传前校验
const beforeUpload = (file) => {
  const isValidType = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
  if (!isValidType) {
    alert('只能上传 JPG/PNG/GIF 格式的图片');
  }
  return isValidType;
};

// 获取图片 URL
// const getImageUrl = (relativePath) => {
//   if (!relativePath) return '';
//   const host = 'http://192.168.0.100:8080'; // 改成你电脑的局域网 IP + 端口
//   return relativePath.startsWith('/upload')
//     ? `${host}${relativePath}`
//     : `${host}/upload/${relativePath}`;
// };


// 提交处理
const handleSubmit = () => {
  const payload = {
    id: form.value.id,
    name: form.value.name,
    point: form.value.point,
    description: form.value.description,
    imageUrl: form.value.imageUrl,
    stock: form.value.stock
  };

  request.post('/api/admin/gift/save', payload).then(() => {
    dialogVisible.value = false;
    fetchData();
  }).catch(error => {
    console.error('请求失败:', error);
  });
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.gift-page {
  padding: 20px;
}
</style>
