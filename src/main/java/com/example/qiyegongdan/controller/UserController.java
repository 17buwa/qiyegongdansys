package com.example.qiyegongdan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.qiyegongdan.annotation.OperLog;
import com.example.qiyegongdan.common.Result;
import com.example.qiyegongdan.dto.UserRequest;
import com.example.qiyegongdan.dto.UserVO;
import com.example.qiyegongdan.security.LoginUser;
import com.example.qiyegongdan.security.SecurityUtil;
import com.example.qiyegongdan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public Result<Page<UserVO>> page(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String roleCode) {
        return Result.success(userService.page(pageNum, pageSize, keyword, roleCode));
    }

    @PostMapping
    @OperLog(module = "user", action = "create")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> create(@Valid @RequestBody UserRequest request) {
        return Result.success("创建成功", userService.create(request));
    }

    @PutMapping("/{id}")
    @OperLog(module = "user", action = "update")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return Result.success("更新成功", userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "user", action = "delete")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile() {
        return Result.success(userService.profile());
    }

    @GetMapping("/me")
    public Result<LoginUser> me() {
        return Result.success(SecurityUtil.getCurrentUser());
    }
}