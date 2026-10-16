import request from './request'

// 登录
export const login = (data) => request.post('/auth/login', data)

// 获取当前用户与权限
export const getProfile = () => request.get('/users/profile')

// 当前登录用户
export const getMe = () => request.get('/users/me')