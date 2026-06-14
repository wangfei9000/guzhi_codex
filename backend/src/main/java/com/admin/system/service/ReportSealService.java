package com.admin.system.service;

import com.admin.system.dto.ReportSealDto;
import com.admin.system.dto.SealListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportSealService {
    void createEmptySeal(Long reportId, Long projectId);
    ReportSealDto uploadSealedReport(Long sealId, String sealedReportUrl, String sealer);
    Page<SealListDto> searchSeals(String reportCode, String projectCode, Pageable pageable);
}
