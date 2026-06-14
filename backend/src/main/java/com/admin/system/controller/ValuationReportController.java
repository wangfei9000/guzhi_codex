package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.ReportListDto;
import com.admin.system.dto.ValuationReportDto;
import com.admin.system.service.PdfGenerationService;
import com.admin.system.service.ValuationReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/valuation-report")
@RequiredArgsConstructor
public class ValuationReportController {

    private final ValuationReportService valuationReportService;
    private final PdfGenerationService pdfGenerationService;

    @GetMapping("/by-project/{projectId}")
    public ApiResponse<List<ValuationReportDto>> listByProjectId(@PathVariable Long projectId) {
        return ApiResponse.success(valuationReportService.listByProjectId(projectId));
    }

    @GetMapping("/latest-downloadable")
    public ApiResponse<ValuationReportDto> getLatestDownloadable(@RequestParam Long projectId) {
        return ApiResponse.success(valuationReportService.getLatestDownloadableByProjectId(projectId));
    }

    @GetMapping("/check-report-code")
    public ApiResponse<Map<String, Boolean>> checkReportCode(@RequestParam String reportCode,
                                                              @RequestParam(required = false) Long excludeId) {
        return ApiResponse.success(Map.of(
                "available", valuationReportService.isReportCodeAvailable(reportCode, excludeId)));
    }

    @PostMapping
    public ApiResponse<ValuationReportDto> create(@RequestParam Long projectId,
                                                   @RequestBody ValuationReportDto dto) {
        return ApiResponse.success("创建成功", valuationReportService.create(projectId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<ValuationReportDto> update(@PathVariable Long id,
                                                   @RequestBody ValuationReportDto dto) {
        return ApiResponse.success("更新成功", valuationReportService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        valuationReportService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @PostMapping("/{id}/generate-pdf")
    public ApiResponse<Map<String, String>> generatePdf(@PathVariable Long id) {
        try {
            String filePath = pdfGenerationService.generatePdf(id);
            ValuationReportDto report = valuationReportService.getById(id);
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("reportUrl", report.getReportUrl());
            if (report.getEndTime() != null) {
                result.put("endTime", report.getEndTime().toString());
            }
            return ApiResponse.success("PDF生成成功", result);
        } catch (Exception e) {
            log.error("PDF generation failed for report {}", id, e);
            return ApiResponse.error("PDF生成失败: " + e.getMessage());
        }
    }

    @GetMapping("/reports")
    public ApiResponse<PageResponse<ReportListDto>> listReports(
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) String address,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReportListDto> page = valuationReportService.searchReports(projectCode, address, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }
}
