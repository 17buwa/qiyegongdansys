package com.example.qiyegongdan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 创建工单请求
 */
@Data
public class TicketCreateRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "描述不能为空")
    private String description;

    /** 优先级：LOW/MEDIUM/HIGH/URGENT */
    private String priority = "MEDIUM";

    @NotNull(message = "工单类型不能为空")
    private Long typeId;

    /** 自定义字段值，key 为 fieldKey */
    private Map<String, Object> fieldValues;
}