package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.annotation.OperLog;
import com.example.qiyegongdan.common.Result;
import com.example.qiyegongdan.entity.TicketAttachment;
import com.example.qiyegongdan.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 附件上传/下载控制器
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final AttachmentService attachmentService;

    @PostMapping("/upload")
    @OperLog(module = "attachment", action = "upload")
    public Result<TicketAttachment> upload(@RequestParam Long ticketId,
                                           @RequestParam("file") MultipartFile file) {
        return Result.success("上传成功", attachmentService.upload(ticketId, file));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String name) {
        String path = attachmentService.getStoredPath(name);
        File file = new File(path);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}