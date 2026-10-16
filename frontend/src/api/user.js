import request from './request'

// 分页查询用户
export const pageUsers = (params) => request.get('/users/page', { params })

// 创建用户
export const createUser = (data) => request.post('/users', data)

// 更新用户
export const updateUser = (id, data) => request.put(`/users/${id}`, data)

// 删除用户
export const deleteUser = (id) => request.delete(`/users/${id}`)