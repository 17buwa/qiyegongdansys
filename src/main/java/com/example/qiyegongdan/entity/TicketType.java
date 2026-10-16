package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工单类型实体
 */
@Data
@TableName("ticket_type")
public class TicketType {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String description;

    /** 处理工单的客服组id（用于自动分配） */
    private Long groupId;

    /** 状态：1启用 0禁用 */
    private Integer status;
}