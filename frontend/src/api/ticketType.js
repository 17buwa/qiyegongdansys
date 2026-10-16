import request from './request'

// 启用的工单类型（含自定义字段）
export const listTypes = () => request.get('/ticket-types')

// 创建类型
export const createType = (data) => request.post('/ticket-types', data)

// 更新类型
export const updateType = (id, data) => request.put(`/ticket-types/${id}`, data)

// 删除类型
export const deleteType = (id) => request.delete(`/ticket-types/${id}`)

// 添加字段
export const addField = (typeId, data) => request.post(`/ticket-types/${typeId}/fields`, data)

// 更新字段
export const updateField = (id, data) => request.put(`/ticket-types/fields/${id}`, data)

// 删除字段
export const deleteField = (id) => request.delete(`/ticket-types/fields/${id}`)