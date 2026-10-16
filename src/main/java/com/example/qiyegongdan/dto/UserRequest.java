package com.example.qiyegongdan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建/更新用户请求
 */
@Data
public class UserRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String name;

    private String email;

    private String phone;

    @NotBlank(message = "角色不能为空")
    private String roleCode;

    private Integer status;
}