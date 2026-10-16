package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单实体
 */
@Data
@TableName("ticket")
public class Ticket {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工单编号 */
    private String ticketNo;

    private String title;

    private String description;

    /** 优先级：LOW/MEDIUM/HIGH/URGENT */
    private String priority;

    /** 状态：NEW/ASSIGNED/PROCESSING/RESOLVED/CLOSED/REJECTED */
    private String status;

    /** 工单类型id */
    private Long typeId;

    /** 创建人id */
    private Long creatorId;

    /** 处理人（客服）id */
    private Long assigneeId;

    /** 客服组id */
    private Long groupId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closedAt;

    /** 解决备注 */
    private String resolveRemark;

    /** 逻辑删除 */
    @TableField(select = false)
    private Integer deleted;
}