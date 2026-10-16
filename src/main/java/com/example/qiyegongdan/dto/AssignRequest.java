package com.example.qiyegongdan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 工单手动/自动分配请求
 */
@Data
public class AssignRequest {

    /** 手动分配：目标客服id */
    private Long assigneeId;

    /** 是否自动分配 */
    private Boolean auto;

    private String remark;
}