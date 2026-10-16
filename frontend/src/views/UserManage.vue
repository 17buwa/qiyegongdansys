<template>
  <div class="page-container">
    <el-card shadow="never" class="mb-16">
      <div class="filter-bar" style="margin-bottom: 0">
        <el-input
          v-model="query.keyword"
          placeholder="搜索用户名 / 姓名"
          clearable
          style="width: 220px"
          @keyup.enter="search"
          @clear="search"
        />
        <el-select v-model="query.roleCode" placeholder="角色" clearable style="width: 140px" @change="search">
          <el-option v-for="(v, k) in ROLE_CONFIG" :key="k" :label="v.label" :value="k" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <div class="flex-spacer" />
        <el-button type="success" :icon="Plus" @click="openEdit()">新增用户</el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="roleType(row.roleCode)" size="small">{{ roleLabel(row.roleCode) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm :title="`确定删除用户 ${row.username} 吗？`" @confirm="remove(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="load"
          @current-change="load"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" type="password" show-password :placeholder="isEdit ? '留空则不修改' : '默认 123456'" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色" prop="roleCode">
          <el-select v-model="form.roleCode" style="width: 100%">
            <el-option v-for="(v, k) in ROLE_CONFIG" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { pageUsers, createUser, updateUser, deleteUser } from '@/api/user'
import { ROLE_CONFIG, roleLabel, roleType } from '@/utils/constants'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', roleCode: '' })
const form = reactive({ username: '', password: '', name: '', email: '', phone: '', roleCode: 'CUSTOMER', status: 1 })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const load = async () => {
  loading.value = true
  try {
    const clean = { ...query }
    if (!clean.keyword) delete clean.keyword
    if (!clean.roleCode) delete clean.roleCode
    const res = await pageUsers(clean)
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  load()
}

const resetForm = () => {
  Object.assign(form, { username: '', password: '', name: '', email: '', phone: '', roleCode: 'CUSTOMER', status: 1 })
}

const openEdit = (row) => {
  resetForm()
  if (row) {
    isEdit.value = true
    editId.value = row.id
    Object.assign(form, {
      username: row.username,
      name: row.name,
      email: row.email,
      phone: row.phone,
      roleCode: row.roleCode,
      status: row.status
    })
  } else {
    isEdit.value = false
    editId.value = null
  }
  dialogVisible.value = true
}

const save = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const payload = { ...form }
      if (!payload.password) delete payload.password
      if (isEdit.value) {
        await updateUser(editId.value, payload)
        ElMessage.success('更新成功')
      } else {
        await createUser(payload)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      load()
    } finally {
      saving.value = false
    }
  })
}

const remove = async (row) => {
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.flex-spacer {
  flex: 1;
}
</style>