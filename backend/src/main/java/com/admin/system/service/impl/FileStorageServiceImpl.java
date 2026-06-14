package com.admin.system.service.impl;

import com.admin.system.entity.FileRecord;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.FileRecordRepository;
import com.admin.system.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final FileRecordRepository fileRecordRepository;

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public FileRecord store(MultipartFile file, Long userId) throws IOException {
        return store(file, userId, null);
    }

    @Override
    @Transactional
    public FileRecord store(MultipartFile file, Long userId, String projectCode) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException("文件为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小超过限制(10MB)");
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("不支持的文件类型: " + contentType);
        }

        String subDir;
        if (StringUtils.hasText(projectCode)) {
            subDir = projectCode;
        } else {
            subDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }

        Path uploadPath = Paths.get(uploadDir, subDir);
        Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path targetPath = uploadPath.resolve(storedName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        FileRecord record = new FileRecord();
        record.setOriginalName(originalName != null ? originalName : "unknown");
        record.setStoredName(storedName);
        record.setFilePath(subDir + "/" + storedName);
        record.setFileSize(file.getSize());
        record.setContentType(contentType);
        record.setUploadUserId(userId);

        log.info("File stored: {} -> {}", originalName, targetPath);
        return fileRecordRepository.save(record);
    }

    @Override
    public Page<FileRecord> listFiles(Pageable pageable) {
        return fileRecordRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        FileRecord record = fileRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        try {
            Path filePath = Paths.get(uploadDir, record.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file on disk: {}", record.getFilePath(), e);
        }

        fileRecordRepository.delete(record);
    }
}
