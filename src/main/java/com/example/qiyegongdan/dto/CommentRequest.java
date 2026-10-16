package com.example.qiyegongdan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 添加评论请求
 */
@Data
public class CommentRequest {

    @NotBlank(message = "评论内容不能为空")
    private String content;

    /** 是否内部评论 */
    private Integer isInternal = 0;
}