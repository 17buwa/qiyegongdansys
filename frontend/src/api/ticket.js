import request from './request'

// 分页查询工单
export const pageTickets = (params) => request.get('/tickets/page', { params })

// 工单详情
export const getTicketDetail = (id) => request.get(`/tickets/${id}`)

// 创建工单
export const createTicket = (data) => request.post('/tickets', data)

// 分配工单
export const assignTicket = (id, data) => request.post(`/tickets/${id}/assign`, data)

// 状态流转
export const changeStatus = (id, data) => request.post(`/tickets/${id}/status`, data)

// 添加评论
export const addComment = (id, data) => request.post(`/tickets/${id}/comments`, data)