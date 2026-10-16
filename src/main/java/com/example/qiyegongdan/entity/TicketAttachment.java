package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单附件实体
 */
@Data
@TableName("ticket_attachment")
public class TicketAttachment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ticketId;

    private Long uploaderId;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}