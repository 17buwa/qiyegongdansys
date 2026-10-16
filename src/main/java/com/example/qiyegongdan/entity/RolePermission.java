package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色-权限关联实体
 */
@Data
@TableName("role_permission")
public class RolePermission {

    private Long roleId;

    private Long permissionId;
}