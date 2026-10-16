package com.example.qiyegongdan.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 工单详情/列表响应
 */
@Data
public class TicketVO {

    private Long id;
    private String ticketNo;
    private String title;
    private String description;
    private String priority;
    private String status;
    private Long typeId;
    private String typeName;
    private Long creatorId;
    private String creatorName;
    private Long assigneeId;
    private String assigneeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    /** 自定义字段值展示 */
    private Map<String, Object> fieldValues;

    /** 评论（详情时返回） */
    private List<Object> comments;

    /** 附件（详情时返回） */
    private List<Object> attachments;
}