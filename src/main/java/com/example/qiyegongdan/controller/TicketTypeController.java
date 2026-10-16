package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.annotation.OperLog;
import com.example.qiyegongdan.common.Result;
import com.example.qiyegongdan.entity.CustomField;
import com.example.qiyegongdan.entity.TicketType;
import com.example.qiyegongdan.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工单类型控制器
 */
@RestController
@RequestMapping("/api/ticket-types")
@RequiredArgsConstructor
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    /** 所有登录用户可见（创建工单需选择类型） */
    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        return Result.success(ticketTypeService.listEnabledTypes());
    }

    @PostMapping
    @OperLog(module = "ticket_type", action = "create")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<TicketType> createType(@RequestBody TicketType type) {
        return Result.success("创建成功", ticketTypeService.createType(type));
    }

    @PutMapping("/{id}")
    @OperLog(module = "ticket_type", action = "update")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<TicketType> updateType(@PathVariable Long id, @RequestBody TicketType type) {
        type.setId(id);
        ticketTypeService.updateType(type);
        return Result.success("更新成功", type);
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "ticket_type", action = "delete")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteType(@PathVariable Long id) {
        ticketTypeService.deleteType(id);
        return Result.success("删除成功", null);
    }

    /** 字段管理 */
    @PostMapping("/{typeId}/fields")
    @OperLog(module = "ticket_type", action = "add_field")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<CustomField> addField(@PathVariable Long typeId, @RequestBody CustomField field) {
        field.setTicketTypeId(typeId);
        ticketTypeService.addField(field);
        return Result.success("添加成功", field);
    }

    @PutMapping("/fields/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateField(@PathVariable Long id, @RequestBody CustomField field) {
        field.setId(id);
        ticketTypeService.updateField(field);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/fields/{id}")
    @OperLog(module = "ticket_type", action = "delete_field")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteField(@PathVariable Long id) {
        ticketTypeService.deleteField(id);
        return Result.success("删除成功", null);
    }
}