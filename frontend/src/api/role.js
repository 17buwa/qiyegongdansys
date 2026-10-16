import request from './request'

// 角色列表
export const listRoles = () => request.get('/roles')

// 权限列表
export const listPermissions = () => request.get('/roles/permissions')

// 某角色的权限
export const getRolePermissions = (roleId) => request.get(`/roles/${roleId}/permissions`)

// 更新角色权限
export const updateRolePermissions = (roleId, permissionIds) =>
  request.put(`/roles/${roleId}/permissions`, { permissionIds })