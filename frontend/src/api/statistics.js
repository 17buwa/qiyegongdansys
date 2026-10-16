import request from './request'

export const getOverview = () => request.get('/statistics/overview')
export const getByStatus = () => request.get('/statistics/by-status')
export const getByPriority = () => request.get('/statistics/by-priority')
export const getByType = () => request.get('/statistics/by-type')
export const getTrend = () => request.get('/statistics/trend')