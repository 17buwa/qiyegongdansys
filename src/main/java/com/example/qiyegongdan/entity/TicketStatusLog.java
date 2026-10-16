package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单状态流转记录实体
 */
@Data
@TableName("ticket_status_log")
public class TicketStatusLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ticketId;

    private String fromStatus;

    private String toStatus;

    private Long operatorId;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}