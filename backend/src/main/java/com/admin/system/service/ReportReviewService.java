package com.admin.system.service;

import com.admin.system.dto.ReportReviewDto;
import java.util.List;

public interface ReportReviewService {
    List<ReportReviewDto> listByReportId(Long reportId);
    ReportReviewDto getById(Long id);
    ReportReviewDto create(Long projectId, Long reportId, ReportReviewDto dto);
    ReportReviewDto update(Long id, ReportReviewDto dto);
    void delete(Long id);
}
