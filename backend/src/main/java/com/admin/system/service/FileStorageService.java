package com.admin.system.service;

import com.admin.system.entity.FileRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    FileRecord store(MultipartFile file, Long userId) throws IOException;
    FileRecord store(MultipartFile file, Long userId, String projectCode) throws IOException;
    Page<FileRecord> listFiles(Pageable pageable);
    void deleteFile(Long id);
}
