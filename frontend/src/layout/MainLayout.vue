<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon :size="22" color="#409eff"><Tickets /></el-icon>
        <span>企业工单系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :router="true"
        background-color="#001529"
        text-color="#a6adb4"
        active-text-color="#fff"
        class="menu"
      >
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.meta.icon" /></el-icon>
          <span>{{ item.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title">{{ currentTitle }}</div>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="30" class="avatar">{{ avatarText }}</el-avatar>
            <span class="name">{{ store.user?.name || store.user?.username }}</span>
            <el-tag size="small" :type="roleType(store.user?.roleCode)">
              {{ roleLabel(store.user?.roleCode) }}
            </el-tag>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { roleLabel, roleType } from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const store = useUserStore()

const menus = computed(() => {
  const routes = router.options.routes.find((r) => r.path === '/')?.children || []
  return routes.filter((r) => !r.meta.hidden && (!r.meta.roles || r.meta.roles.includes(store.user?.roleCode)))
})

const activeMenu = computed(() => {
  if (route.path.startsWith('/tickets')) {
    return '/tickets'
  }
  return route.path
})

const currentTitle = computed(() => route.meta.title || '')

const avatarText = computed(() => {
  const n = store.user?.name || store.user?.username || 'U'
  return n.charAt(0).toUpperCase()
})

const handleCommand = async (cmd) => {
  if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'logout') {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    store.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}

.aside {
  background-color: #001529;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.menu {
  border-right: none;
  flex: 1;
}
.menu :deep(.el-menu-item.is-active) {
  background-color: #409eff;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e5e6eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}

.avatar {
  background: #409eff;
}

.name {
  font-size: 14px;
  color: #303133;
}

.main {
  background: #f5f7fa;
  overflow-y: auto;
  padding: 0;
}
</style>