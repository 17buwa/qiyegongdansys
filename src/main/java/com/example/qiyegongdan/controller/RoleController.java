package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.annotation.OperLog;
import com.example.qiyegongdan.common.Result;
import com.example.qiyegongdan.entity.Permission;
import com.example.qiyegongdan.entity.Role;
import com.example.qiyegongdan.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色权限控制器
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RolePermissionService rolePermissionService;

    @GetMapping
    public Result<List<Role>> listRoles() {
        return Result.success(rolePermissionService.listRoles());
    }

    @GetMapping("/permissions")
    public Result<List<Permission>> listPermissions() {
        return Result.success(rolePermissionService.listPermissions());
    }

    @GetMapping("/{roleId}/permissions")
    public Result<List<Permission>> getRolePermissions(@PathVariable Long roleId) {
        return Result.success(rolePermissionService.getRolePermissions(roleId));
    }

    @PutMapping("/{roleId}/permissions")
    @OperLog(module = "role", action = "update_permissions")
    public Result<Void> updateRolePermissions(@PathVariable Long roleId,
                                              @RequestBody Map<String, List<Long>> body) {
        rolePermissionService.updateRolePermissions(roleId, body.get("permissionIds"));
        return Result.success();
    }
}