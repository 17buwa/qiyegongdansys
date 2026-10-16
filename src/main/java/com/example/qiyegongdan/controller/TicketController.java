package com.example.qiyegongdan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.qiyegongdan.annotation.OperLog;
import com.example.qiyegongdan.common.Result;
import com.example.qiyegongdan.dto.AssignRequest;
import com.example.qiyegongdan.dto.CommentRequest;
import com.example.qiyegongdan.dto.StatusChangeRequest;
import com.example.qiyegongdan.dto.TicketCreateRequest;
import com.example.qiyegongdan.dto.TicketVO;
import com.example.qiyegongdan.entity.TicketComment;
import com.example.qiyegongdan.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 工单控制器
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/page")
    public Result<Page<TicketVO>> page(@RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) Map<String, Object> filters) {
        return Result.success(ticketService.page(pageNum, pageSize, filters));
    }

    @GetMapping("/{id}")
    public Result<TicketVO> detail(@PathVariable Long id) {
        return Result.success(ticketService.detail(id));
    }

    @PostMapping
    @OperLog(module = "ticket", action = "create")
    public Result<TicketVO> create(@Valid @RequestBody TicketCreateRequest request) {
        return Result.success("工单创建成功", ticketService.create(request));
    }

    @PostMapping("/{id}/assign")
    @OperLog(module = "ticket", action = "assign")
    public Result<TicketVO> assign(@PathVariable Long id, @RequestBody AssignRequest request) {
        return Result.success("分配成功", ticketService.assign(id, request));
    }

    @PostMapping("/{id}/status")
    @OperLog(module = "ticket", action = "change_status")
    public Result<TicketVO> changeStatus(@PathVariable Long id,
                                         @Valid @RequestBody StatusChangeRequest request) {
        return Result.success("状态更新成功", ticketService.changeStatus(id, request.getToStatus(), request.getRemark()));
    }

    @PostMapping("/{id}/comments")
    @OperLog(module = "ticket", action = "add_comment")
    public Result<TicketComment> addComment(@PathVariable Long id,
                                            @Valid @RequestBody CommentRequest request) {
        return Result.success("评论成功", ticketService.addComment(id, request));
    }
}