package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** 密码（BCrypt 加密，不返回前端） */
    @JsonIgnore
    private String password;

    private String name;

    private String email;

    private String phone;

    private String avatar;

    /** 角色编码，如 ADMIN / AGENT / CUSTOMER */
    private String roleCode;

    /** 状态：1启用 0禁用 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 逻辑删除：0正常 1已删除 */
    @TableField(select = false)
    private Integer deleted;
}