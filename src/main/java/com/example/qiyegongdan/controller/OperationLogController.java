package com.example.qiyegongdan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.qiyegongdan.common.Result;
import com.example.qiyegongdan.entity.OperationLog;
import com.example.qiyegongdan.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OperationLogController {

    private final OperationLogMapper operationLogMapper;

    @GetMapping("/page")
    public Result<Page<OperationLog>> page(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String module,
                                           @RequestParam(required = false) String username) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (StringUtils.hasText(username)) {
            wrapper.like(OperationLog::getUsername, username);
        }
        wrapper.orderByDesc(OperationLog::getId);
        return Result.success(operationLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper));
    }
}