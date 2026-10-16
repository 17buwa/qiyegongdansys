package com.example.qiyegongdan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qiyegongdan.entity.Permission;
import com.example.qiyegongdan.entity.Role;
import com.example.qiyegongdan.entity.RolePermission;
import com.example.qiyegongdan.mapper.PermissionMapper;
import com.example.qiyegongdan.mapper.RoleMapper;
import com.example.qiyegongdan.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色权限服务
 */
@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public List<Role> listRoles() {
        return roleMapper.selectList(null);
    }

    public List<Permission> listPermissions() {
        return permissionMapper.selectList(null);
    }

    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, roleId));
        if (permissionIds != null) {
            for (Long pid : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(pid);
                rolePermissionMapper.insert(rp);
            }
        }
    }

    public List<Permission> getRolePermissions(Long roleId) {
        return permissionMapper.selectByRoleCode(null).stream()
                .filter(p -> rolePermissionMapper.selectCount(
                        new LambdaQueryWrapper<RolePermission>()
                                .eq(RolePermission::getRoleId, roleId)
                                .eq(RolePermission::getPermissionId, p.getId())) > 0)
                .toList();
    }
}