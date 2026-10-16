// 工单状态展示配置
export const STATUS_CONFIG = {
  NEW: { label: '新建', type: 'info' },
  ASSIGNED: { label: '已分配', type: 'primary' },
  PROCESSING: { label: '处理中', type: 'warning' },
  RESOLVED: { label: '已解决', type: 'success' },
  CLOSED: { label: '已关闭', type: 'info' },
  REJECTED: { label: '已驳回', type: 'danger' }
}

// 优先级展示配置
export const PRIORITY_CONFIG = {
  LOW: { label: '低', type: 'info' },
  MEDIUM: { label: '中', type: 'primary' },
  HIGH: { label: '高', type: 'warning' },
  URGENT: { label: '紧急', type: 'danger' }
}

// 角色展示配置
export const ROLE_CONFIG = {
  ADMIN: { label: '管理员', type: 'danger' },
  AGENT: { label: '客服', type: 'warning' },
  CUSTOMER: { label: '普通用户', type: 'info' }
}

// 状态机允许的流转
export const TRANSITIONS = {
  NEW: ['ASSIGNED', 'REJECTED', 'CLOSED'],
  ASSIGNED: ['PROCESSING', 'REJECTED'],
  PROCESSING: ['RESOLVED', 'REJECTED'],
  RESOLVED: ['CLOSED', 'PROCESSING'],
  REJECTED: ['ASSIGNED', 'PROCESSING', 'CLOSED'],
  CLOSED: []
}

// 字段类型
export const FIELD_TYPES = [
  { value: 'text', label: '单行文本' },
  { value: 'textarea', label: '多行文本' },
  { value: 'number', label: '数字' },
  { value: 'select', label: '下拉选择' },
  { value: 'date', label: '日期' }
]

export function statusLabel(s) {
  return STATUS_CONFIG[s]?.label || s
}
export function statusType(s) {
  return STATUS_CONFIG[s]?.type || 'info'
}
export function priorityLabel(p) {
  return PRIORITY_CONFIG[p]?.label || p
}
export function priorityType(p) {
  return PRIORITY_CONFIG[p]?.type || 'info'
}
export function roleLabel(r) {
  return ROLE_CONFIG[r]?.label || r
}
export function roleType(r) {
  return ROLE_CONFIG[r]?.type || 'info'
}

// 公告类型展示配置
export const NOTICE_TYPE_CONFIG = {
  SYSTEM: { label: '系统公告', type: 'danger' },
  MAINTENANCE: { label: '维护通知', type: 'warning' },
  RULES: { label: '制度规范', type: 'primary' },
  OTHER: { label: '其他', type: 'info' }
}

export function noticeLabel(t) {
  return NOTICE_TYPE_CONFIG[t]?.label || t
}
export function noticeType(t) {
  return NOTICE_TYPE_CONFIG[t]?.type || 'info'
}