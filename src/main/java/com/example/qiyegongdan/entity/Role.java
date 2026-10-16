package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色实体
 */
@Data
@TableName("roles")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编码：ADMIN/AGENT/CUSTOMER */
    private String roleCode;

    /** 角色名称：管理员/客服/普通用户 */
    private String roleName;

    private String remark;
}