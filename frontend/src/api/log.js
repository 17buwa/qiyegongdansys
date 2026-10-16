import request from './request'

export const pageLogs = (params) => request.get('/logs/page', { params })