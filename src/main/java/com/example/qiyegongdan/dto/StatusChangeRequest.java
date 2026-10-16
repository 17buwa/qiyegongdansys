package com.example.qiyegongdan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 工单状态流转请求
 */
@Data
public class StatusChangeRequest {

    @NotBlank(message = "目标状态不能为空")
    private String toStatus;

    private String remark;
}