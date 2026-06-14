package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.ReportTemplateDto;
import com.admin.system.service.ReportTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report-template")
@RequiredArgsConstructor
public class ReportTemplateController {

    private final ReportTemplateService reportTemplateService;

    @GetMapping
    public ApiResponse<PageResponse<ReportTemplateDto>> list(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ReportTemplateDto> page = reportTemplateService.list(pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @PostMapping
    public ApiResponse<ReportTemplateDto> create(@Valid @RequestBody ReportTemplateDto dto) {
        return ApiResponse.success("创建成功", reportTemplateService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReportTemplateDto> update(@PathVariable Long id,
                                                  @RequestBody ReportTemplateDto dto) {
        return ApiResponse.success("更新成功", reportTemplateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        reportTemplateService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
