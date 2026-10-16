package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单评论实体
 */
@Data
@TableName("ticket_comment")
public class TicketComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ticketId;

    private Long userId;

    private String content;

    /** 是否内部评论：1内部 0公开 */
    private Integer isInternal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}