package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.entity.FileRecord;
import com.admin.system.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileRecordController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ApiResponse<FileRecord> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam(required = false) String projectCode,
                                               Principal principal) throws IOException {
        FileRecord record = fileStorageService.store(file, 1L, projectCode); // Default user ID
        return ApiResponse.success("上传成功", record);
    }

    @GetMapping
    public ApiResponse<PageResponse<FileRecord>> listFiles(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<FileRecord> page = fileStorageService.listFiles(pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFile(@PathVariable Long id) {
        fileStorageService.deleteFile(id);
        return ApiResponse.success("删除成功", null);
    }
}
