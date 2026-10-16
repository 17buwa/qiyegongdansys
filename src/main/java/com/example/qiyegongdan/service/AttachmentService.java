package com.example.qiyegongdan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qiyegongdan.entity.TicketAttachment;
import com.example.qiyegongdan.mapper.TicketAttachmentMapper;
import com.example.qiyegongdan.security.LoginUser;
import com.example.qiyegongdan.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 附件服务：本地磁盘存储
 */
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final TicketAttachmentMapper ticketAttachmentMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public TicketAttachment upload(Long ticketId, MultipartFile file) {
        LoginUser user = SecurityUtil.getCurrentUser();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf('.')) : "";
            String storedName = UUID.randomUUID().toString().replace("-", "") + ext;

            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Path target = dir.resolve(storedName);
            file.transferTo(target);

            TicketAttachment attachment = new TicketAttachment();
            attachment.setTicketId(ticketId);
            attachment.setUploaderId(user.getId());
            attachment.setFileName(originalName);
            // 存储相对路径，供下载接口拼装
            attachment.setFilePath("/api/files/download?name=" + storedName);
            attachment.setFileSize(file.getSize());
            attachment.setFileType(ext);
            attachment.setCreatedAt(LocalDateTime.now());
            ticketAttachmentMapper.insert(attachment);
            return attachment;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage(), e);
        }
    }

    public String getStoredPath(String storedName) {
        return Paths.get(uploadDir).toAbsolutePath().normalize().resolve(storedName).toString();
    }
}