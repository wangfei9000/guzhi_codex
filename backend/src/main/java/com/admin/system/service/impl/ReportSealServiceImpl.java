package com.admin.system.service.impl;

import com.admin.system.dto.ReportSealDto;
import com.admin.system.dto.SealListDto;
import com.admin.system.entity.ReportSeal;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.ReportSealRepository;
import com.admin.system.service.ReportSealService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportSealServiceImpl implements ReportSealService {

    private final ReportSealRepository reportSealRepository;

    @Override
    public void createEmptySeal(Long reportId, Long projectId) {
        ReportSeal seal = new ReportSeal();
        seal.setReportId(reportId);
        seal.setProjectId(projectId);
        reportSealRepository.save(seal);
    }

    @Override
    public ReportSealDto uploadSealedReport(Long sealId, String sealedReportUrl, String sealer) {
        ReportSeal seal = reportSealRepository.findById(sealId)
                .orElseThrow(() -> new BusinessException("盖章记录不存在"));
        seal.setSealedReportUrl(sealedReportUrl);
        seal.setSealer(sealer);
        seal.setSealDate(LocalDate.now());
        return toDto(reportSealRepository.save(seal));
    }

    @Override
    public Page<SealListDto> searchSeals(String reportCode, String projectCode, Pageable pageable) {
        String codeKw = StringUtils.hasText(reportCode) ? "%" + reportCode + "%" : null;
        String projKw = StringUtils.hasText(projectCode) ? "%" + projectCode + "%" : null;
        return reportSealRepository.searchSeals(codeKw, projKw, pageable);
    }

    private ReportSealDto toDto(ReportSeal s) {
        ReportSealDto dto = new ReportSealDto();
        dto.setId(s.getId());
        dto.setReportId(s.getReportId());
        dto.setProjectId(s.getProjectId());
        dto.setSealedReportUrl(s.getSealedReportUrl());
        dto.setSealer(s.getSealer());
        dto.setSealDate(s.getSealDate());
        return dto;
    }
}
