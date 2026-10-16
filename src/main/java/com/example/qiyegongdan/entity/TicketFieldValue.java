package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工单自定义字段值实体
 */
@Data
@TableName("ticket_field_value")
public class TicketFieldValue {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ticketId;

    private Long fieldId;

    private String fieldKey;

    private String fieldValue;
}