package com.admin.system.service;

import com.admin.system.dto.ReportListDto;
import com.admin.system.dto.ValuationReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ValuationReportService {
    List<ValuationReportDto> listByProjectId(Long projectId);
    ValuationReportDto getById(Long id);
    ValuationReportDto getLatestDownloadableByProjectId(Long projectId);
    ValuationReportDto create(Long projectId, ValuationReportDto dto);
    ValuationReportDto update(Long id, ValuationReportDto dto);
    void delete(Long id);
    Page<ReportListDto> searchReports(String projectCode, String address, Pageable pageable);
    boolean isReportCodeAvailable(String reportCode, Long excludeId);
}
