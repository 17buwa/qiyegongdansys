package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单分配记录实体
 */
@Data
@TableName("ticket_assign")
public class TicketAssign {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ticketId;

    private Long operatorId;

    private Long assigneeId;

    /** 分配模式：AUTO自动 MANUAL手动 */
    private String mode;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}