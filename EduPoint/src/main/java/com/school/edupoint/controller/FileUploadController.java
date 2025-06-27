package com.school.edupoint.controller;

import com.school.edupoint.response.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    // application.properties 中配置的文件存储路径
    @Value("${file.upload-dir:upload}")
    private String uploadDir;

    /**
     * 单文件上传接口
     */
    @PostMapping("/poster")
    public Result<String> uploadPoster(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "请选择要上传的文件");
        }

        try {
            // 创建上传目录（如果不存在）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名（防止重名）
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID() + fileExtension;

            // 文件保存路径
            Path filePath = uploadPath.resolve(newFileName);

            // 保存文件到目标路径
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回访问路径（前端可拼接为完整 URL）
            return Result.success("/upload/" + newFileName);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(500, "文件上传失败：" + e.getMessage());
        }
    }
}
