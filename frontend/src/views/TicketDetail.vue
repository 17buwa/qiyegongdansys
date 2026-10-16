<template>
  <div class="page-container" v-loading="loading">
    <el-card shadow="never" v-if="ticket">
      <!-- 头部信息 -->
      <template #header>
        <div class="detail-header">
          <el-button link :icon="ArrowLeft" @click="router.back()">返回</el-button>
          <div class="title-row">
            <span class="ticket-no">{{ ticket.ticketNo }}</span>
            <span class="title">{{ ticket.title }}</span>
            <el-tag :type="statusType(ticket.status)" size="small">{{ statusLabel(ticket.status) }}</el-tag>
            <el-tag :type="priorityType(ticket.priority)" size="small">{{ priorityLabel(ticket.priority) }}</el-tag>
          </div>
          <div class="header-ops">
            <el-button
              v-if="canAssign"
              type="primary"
              :icon="Avatar"
              @click="openAssign"
            >分配</el-button>
            <el-dropdown v-if="canChangeStatus" style="margin-left: 8px" @command="openTransition">
              <el-button type="warning" :icon="Right">状态流转</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="st in nextStatuses"
                    :key="st"
                    :command="st"
                  >{{ statusLabel(st) }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="类型">{{ ticket.typeName }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ ticket.creatorName }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ ticket.assigneeName || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ ticket.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="解决时间">{{ ticket.resolvedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关闭时间">{{ ticket.closedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">
          <div style="white-space: pre-wrap">{{ ticket.description }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 自定义字段值 -->
      <template v-if="fieldList.length">
        <el-divider content-position="left">附加信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item v-for="f in fieldList" :key="f.id" :label="f.fieldName">
            {{ formatField(f) }}
          </el-descriptions-item>
        </el-descriptions>
      </template>

      <!-- 附件 -->
      <el-divider content-position="left">附件</el-divider>
      <div class="attach-row">
        <div v-for="a in attachments" :key="a.id" class="attach-item">
          <el-link type="primary" :href="`/api/files/download?name=${a.fileName}`" target="_blank">
            <el-icon><Paperclip /></el-icon> {{ a.fileName }}
          </el-link>
          <span class="text-muted">({{ formatSize(a.fileSize) }})</span>
        </div>
        <el-upload
          :action="`/api/files/upload`"
          :data="{ ticketId: ticket.id }"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="onUploadSuccess"
          :on-error="onUploadError"
          v-if="canOperate"
        >
          <el-button :icon="UploadFilled">上传附件</el-button>
        </el-upload>
        <span v-if="attachments.length === 0 && !canOperate" class="text-muted">暂无附件</span>
      </div>

      <!-- 评论 -->
      <el-divider content-position="left">评论与回复</el-divider>
      <div class="comment-list">
        <div v-for="c in comments" :key="c.id" class="comment-item">
          <el-avatar :size="32" class="comment-avatar">{{ firstChar(c.userName) }}</el-avatar>
          <div class="comment-body">
            <div class="comment-meta">
              <span class="comment-user">{{ c.userName }}</span>
              <el-tag v-if="c.isInternal === 1" size="small" type="warning">内部</el-tag>
              <span class="text-muted">{{ c.createdAt }}</span>
            </div>
            <div class="comment-content">{{ c.content }}</div>
          </div>
        </div>
        <span v-if="comments.length === 0" class="text-muted">暂无评论</span>
      </div>

      <el-divider />
      <div class="comment-input" v-if="canOperate">
        <el-input
          v-model="commentForm.content"
          type="textarea"
          :rows="3"
          maxlength="2000"
          show-word-limit
          placeholder="输入评论内容"
        />
        <div class="comment-toolbar">
          <el-checkbox v-model="commentForm.isInternal">内部评论（仅客服可见）</el-checkbox>
          <el-button type="primary" :disabled="!commentForm.content" :loading="commenting" @click="submitComment">
            发表评论
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 分配弹窗 -->
    <el-dialog v-model="assignVisible" title="分配工单" width="480px">
      <el-form v-model="assignForm" label-width="90px">
        <el-form-item label="分配方式">
          <el-switch
            v-model="assignForm.auto"
            active-text="自动分配"
            inactive-text="手动分配"
          />
        </el-form-item>
        <el-form-item v-if="!assignForm.auto" label="客服">
          <el-select v-model="assignForm.assigneeId" placeholder="选择客服" style="width: 100%">
            <el-option v-for="a in agents" :key="a.id" :label="`${a.name || a.username}`" :value="a.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assigning" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 状态流转弹窗 -->
    <el-dialog v-model="transitionVisible" :title="`流转至 ${statusLabel(nextTarget)}`" width="460px">
      <el-input v-model="transitionRemark" type="textarea" :rows="3" placeholder="备注（可选）" />
      <template #footer>
        <el-button @click="transitionVisible = false">取消</el-button>
        <el-button type="primary" :loading="transitioning" @click="submitTransition">确认流转</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Avatar, Right, Paperclip, UploadFilled } from '@element-plus/icons-vue'
import { getTicketDetail, changeStatus, assignTicket, addComment } from '@/api/ticket'
import { pageUsers } from '@/api/user'
import { useUserStore } from '@/stores/user'
import {
  STATUS_CONFIG,
  TRANSITIONS,
  statusLabel,
  statusType,
  priorityLabel,
  priorityType
} from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const ticketId = route.params.id

const loading = ref(false)
const commenting = ref(false)
const assigning = ref(false)
const transitioning = ref(false)
const ticket = ref(null)
const comments = ref([])
const attachments = ref([])
const fieldList = ref([])
const agents = ref([])
const assignVisible = ref(false)
const transitionVisible = ref(false)
const nextTarget = ref('')
const transitionRemark = ref('')

const assignForm = reactive({ auto: true, assigneeId: null, remark: '' })
const commentForm = reactive({ content: '', isInternal: false })

// 后端内部评论 isInternal=1 仅客服可见；前端简化：客服/管理员可操作
const canOperate = computed(() => store.isAdmin || store.isAgent)
const canAssign = computed(() => store.isAdmin || store.isAgent)
const canChangeStatus = computed(() => (store.isAdmin || store.isAgent) && nextStatuses.value.length > 0)
const nextStatuses = computed(() => TRANSITIONS[ticket.value?.status] || [])
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('token')}` }))

const firstChar = (s) => (s || '?').charAt(0)

const formatSize = (bytes) => {
  if (bytes == null) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}

const formatField = (f) => {
  const v = ticket.value.fieldValues?.[f.fieldKey]
  if (v == null || v === '') return '-'
  return v
}

const load = async () => {
  loading.value = true
  try {
    const res = await getTicketDetail(ticketId)
    const data = res.data
    ticket.value = data
    comments.value = data.comments || []
    attachments.value = data.attachments || []
    const typeRes = await getTypes()
    const t = typeRes.find((i) => i.id === data.typeId)
    fieldList.value = t?.fields || []
  } finally {
    loading.value = false
  }
}

const getTypes = async () => {
  const { listTypes } = await import('@/api/ticketType')
  const res = await listTypes()
  return res.data
}

const openAssign = async () => {
  assignForm.auto = true
  assignForm.assigneeId = null
  assignForm.remark = ''
  if (agents.value.length === 0) {
    const res = await pageUsers({ pageNum: 1, pageSize: 100, roleCode: 'AGENT' })
    agents.value = res.data.records
  }
  assignVisible.value = true
}

const submitAssign = async () => {
  assigning.value = true
  try {
    const payload = {
      auto: assignForm.auto,
      assigneeId: assignForm.auto ? null : assignForm.assigneeId,
      remark: assignForm.remark
    }
    await assignTicket(ticketId, payload)
    ElMessage.success('分配成功')
    assignVisible.value = false
    load()
  } finally {
    assigning.value = false
  }
}

const openTransition = (st) => {
  nextTarget.value = st
  transitionRemark.value = ''
  transitionVisible.value = true
}

const submitTransition = async () => {
  transitioning.value = true
  try {
    await changeStatus(ticketId, { toStatus: nextTarget.value, remark: transitionRemark.value })
    ElMessage.success('状态更新成功')
    transitionVisible.value = false
    load()
  } finally {
    transitioning.value = false
  }
}

const submitComment = async () => {
  commenting.value = true
  try {
    await addComment(ticketId, {
      content: commentForm.content,
      isInternal: commentForm.isInternal ? 1 : 0
    })
    commentForm.content = ''
    commentForm.isInternal = false
    ElMessage.success('评论成功')
    load()
  } finally {
    commenting.value = false
  }
}

const onUploadSuccess = (res) => {
  if (res.code === 200) {
    ElMessage.success('上传成功')
    load()
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const onUploadError = () => {
  ElMessage.error('上传失败')
}

onMounted(load)
</script>

<style scoped>
.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
}
.title-row {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}
.ticket-no {
  font-weight: 600;
  color: #409eff;
}
.title {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.attach-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
}
.attach-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.comment-list {
  margin-bottom: 8px;
}
.comment-item {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
}
.comment-avatar {
  background: #409eff;
  flex-shrink: 0;
}
.comment-body {
  flex: 1;
  background: #f5f7fa;
  border-radius: 6px;
  padding: 8px 12px;
}
.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.comment-user {
  font-weight: 600;
  color: #303133;
}
.comment-content {
  white-space: pre-wrap;
  color: #303133;
}
.comment-input {
  margin-top: 8px;
}
.comment-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}
</style>