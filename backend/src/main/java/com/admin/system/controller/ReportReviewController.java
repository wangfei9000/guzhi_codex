package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.ReportReviewDto;
import com.admin.system.service.ReportReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report-review")
@RequiredArgsConstructor
public class ReportReviewController {

    private final ReportReviewService reportReviewService;

    @GetMapping("/by-report/{reportId}")
    public ApiResponse<List<ReportReviewDto>> listByReportId(@PathVariable Long reportId) {
        return ApiResponse.success(reportReviewService.listByReportId(reportId));
    }

    @PostMapping
    public ApiResponse<ReportReviewDto> create(@RequestParam Long projectId,
                                                @RequestParam Long reportId,
                                                @RequestBody ReportReviewDto dto) {
        return ApiResponse.success("创建成功", reportReviewService.create(projectId, reportId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReportReviewDto> update(@PathVariable Long id,
                                                @RequestBody ReportReviewDto dto) {
        return ApiResponse.success("更新成功", reportReviewService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        reportReviewService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
