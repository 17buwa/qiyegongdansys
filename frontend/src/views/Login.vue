<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="32" color="#409eff"><Tickets /></el-icon>
        <h2>企业工单系统</h2>
        <p>请登录您的账号</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
      <div class="login-tip text-muted">
        默认账号：admin / agent01 / user01（密码均为 123456）
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const store = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: '123456'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await store.login(form)
    await store.loadProfile()
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f3a93 0%, #409eff 100%);
}

.login-card {
  width: 400px;
  background: #fff;
  border-radius: 10px;
  padding: 40px 36px 28px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 28px;
}
.login-header h2 {
  margin: 10px 0 4px;
  font-size: 22px;
  color: #303133;
}
.login-header p {
  color: #909399;
  font-size: 13px;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
}

.login-tip {
  text-align: center;
  margin-top: 18px;
}
</style>