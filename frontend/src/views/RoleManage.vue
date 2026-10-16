<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>角色列表</template>
          <div v-loading="roleLoading">
            <div
              v-for="r in roles"
              :key="r.id"
              class="role-item"
              :class="{ active: currentRole?.id === r.id }"
              @click="selectRole(r)"
            >
              <div class="role-name">{{ r.roleName }}</div>
              <el-tag size="small" :type="roleType(r.roleCode)">{{ r.roleCode }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div style="display: flex; align-items: center; justify-content: space-between">
              <span>权限配置：{{ currentRole?.roleName || '请选择角色' }}</span>
              <el-button type="primary" size="small" :disabled="!currentRole" @click="selectAll">全选</el-button>
            </div>
          </template>

          <el-checkbox-group v-model="selectedPerms" :disabled="!currentRole">
            <div v-for="perm in permissions" :key="perm.id" class="perm-item">
              <el-checkbox :value="perm.id" :label="`${perm.permName}(${perm.permCode})`" />
            </div>
          </el-checkbox-group>

          <div class="save-bar">
            <el-button
              type="primary"
              :loading="saving"
              :disabled="!currentRole"
              @click="save"
            >保存权限</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listRoles,
  listPermissions,
  getRolePermissions,
  updateRolePermissions
} from '@/api/role'
import { roleLabel, roleType } from '@/utils/constants'

const roles = ref([])
const permissions = ref([])
const currentRole = ref(null)
const selectedPerms = ref([])
const roleLoading = ref(false)
const saving = ref(false)

const load = async () => {
  roleLoading.value = true
  try {
    const [r, p] = await Promise.all([listRoles(), listPermissions()])
    roles.value = r.data
    permissions.value = p.data
    if (roles.value.length && !currentRole.value) {
      selectRole(roles.value[0])
    } else if (currentRole.value) {
      const cur = roles.value.find((x) => x.id === currentRole.value.id)
      if (cur) selectRole(cur)
    }
  } finally {
    roleLoading.value = false
  }
}

const selectRole = async (role) => {
  currentRole.value = role
  const res = await getRolePermissions(role.id)
  selectedPerms.value = (res.data || []).map((p) => p.id)
}

const selectAll = () => {
  selectedPerms.value = permissions.value.map((p) => p.id)
}

const save = async () => {
  saving.value = true
  try {
    await updateRolePermissions(currentRole.value.id, selectedPerms.value)
    ElMessage.success('权限保存成功')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.role-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-radius: 6px;
  margin-bottom: 8px;
  cursor: pointer;
  border: 1px solid #e5e6eb;
  transition: all 0.2s;
}
.role-item:hover {
  border-color: #409eff;
}
.role-item.active {
  border-color: #409eff;
  background: rgba(64, 158, 255, 0.08);
}
.role-name {
  font-weight: 600;
}
.perm-item {
  padding: 10px 12px;
  border-bottom: 1px solid #f0f0f0;
}
.save-bar {
  margin-top: 16px;
  text-align: right;
}
</style>