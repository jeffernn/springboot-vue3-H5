<script setup lang="ts">
import UserAPI from "@/api/user";

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/29 14:24
 */
const name = ref<string>("");
const handleQuery = () => {
  loadData(1);
  name.value = "";
};

// 修改
const handleOpenDialog = async (id?: number) => {
  if (id) {
    formData.value = await UserAPI.getUser(id);
    dialog.value.visible = true;
  } else {
    dialog.value.visible = true;
  }
};
const handleDelete = (id: number) => {
  ElMessageBox.confirm("确定删除吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
    lockScroll: false,
  }).then(() => {
    UserAPI.deleteUser({ id }).then(() => {
      ElMessage.success("删除成功");
      loadData(0);
    });
  });
};

const loading = ref(false);
const pageData = ref<any>();

const handleSelectionChange = () => {};

const total = ref<number>(0);
const pageNum = ref(1);
const pageSize = ref(10);
const fetchData = ref();

// 分页加载数据
const loadDataPage = async (
  username: string,
  pageNum: number,
  pageSize: number
) => {
  return await UserAPI.getPage(username, pageSize, pageNum);
};

const loadData = (num: number) => {
  pageNum.value = num;
  loadDataPage(name.value, num, pageSize.value).then((data) => {
    total.value = data.total;
    fetchData.value = data.records;
    pageData.value = data.records;
  });
};

loadDataPage(name.value, pageNum.value, pageSize.value).then((data) => {
  total.value = data.total;
  fetchData.value = data.records;
  pageData.value = data.records;
});

// 新增 修改
const userFormRef = ref(); // 表单
const handleSubmit = () => {
  if (userFormRef.value) {
    userFormRef.value.validate((valid: boolean) => {
      if (valid) {
        // 验证成功
        UserAPI.saveUser(formData.value).then((data) => {
          if (formData.value.id) {
            // 修改
            ElMessage.success("修改用户成功");
            handleCloseDialog();
          } else {
            // 新增
            ElMessage.success("新增用户成功");
            handleCloseDialog();
          }
          loadData(1);
        });
      }
    });
  }
};
const handleCloseDialog = () => {
  dialog.value.visible = false;
};

const formData = ref<any>({
  username: "",
  password: "",
});
const drawerSize = ref<number>(500);
const dialog = ref({
  visible: false,
  title: "用户",
});

const rules = reactive({
  username: [{ required: true, message: "用户名不能为空", trigger: "blur" }],
  password: [{ required: true, message: "密码不能为空", trigger: "blur" }],
});
</script>

<template>
  <div class="app-container">
    <div class="search-container">
      <el-form :inline="true">
        <el-form-item label="关键字" prop="keywords">
          <el-input
            v-model="name"
            placeholder="用户名"
            style="width: 240px"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item class="search-buttons">
          <el-button type="primary" icon="search" @click="handleQuery">
            搜索
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table-container">
      <el-card shadow="hover" class="data-table">
        <div class="data-table__toolbar">
          <el-button type="success" icon="plus" @click="handleOpenDialog()">
            新增
          </el-button>
        </div>

        <el-table
          v-loading="loading"
          :data="pageData"
          border
          stripe
          highlight-current-row
          class="data-table__content"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="用户名" prop="username" />
          <el-table-column label="密码" width="150" align="center">
            <template #default="scope">
              {{ "*".repeat(scope.row.password.length) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" fixed="right" width="220">
            <template #default="scope">
              <el-button
                type="primary"
                icon="edit"
                link
                size="small"
                @click="handleOpenDialog(scope.row.id)"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                icon="delete"
                link
                size="small"
                @click="handleDelete(scope.row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="data-el-pagination">
          <el-pagination
            v-if="total > 0"
            v-model:total="total"
            v-model:page="pageNum"
            v-model:limit="pageSize"
            @pagination="fetchData"
          />
        </div>
      </el-card>
    </div>

    <!-- 用户表单 -->
    <el-drawer
      v-model="dialog.visible"
      :title="dialog.title"
      append-to-body
      :size="drawerSize"
      @close="handleCloseDialog"
    >
      <el-form
        ref="userFormRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            :readonly="!!formData.id"
            placeholder="请输入用户名"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            type="password"
            v-model="formData.password"
            placeholder="请输入密码"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
          <el-button @click="handleCloseDialog">取 消</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<style scoped lang="scss"></style>
