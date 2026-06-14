package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.ReportSealDto;
import com.admin.system.dto.SealListDto;
import com.admin.system.service.ReportSealService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/report-seal")
@RequiredArgsConstructor
public class ReportSealController {

    private final ReportSealService reportSealService;

    @GetMapping("/seals")
    public ApiResponse<PageResponse<SealListDto>> listSeals(
            @RequestParam(required = false) String reportCode,
            @RequestParam(required = false) String projectCode,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<SealListDto> page = reportSealService.searchSeals(reportCode, projectCode, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @PutMapping("/{id}/upload")
    public ApiResponse<ReportSealDto> uploadSealedReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Principal principal) {
        String username = principal != null ? principal.getName() : "system";
        ReportSealDto dto = reportSealService.uploadSealedReport(id, body.get("sealedReportUrl"), username);
        return ApiResponse.success("盖章报告上传成功", dto);
    }
}
