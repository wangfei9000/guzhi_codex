package com.admin.system.service.impl;

import com.admin.system.dto.ReportListDto;
import com.admin.system.dto.ValuationReportDto;
import com.admin.system.entity.Project;
import com.admin.system.entity.ValuationReport;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.ProjectRepository;
import com.admin.system.repository.ValuationReportRepository;
import com.admin.system.service.ReportSealService;
import com.admin.system.service.ValuationReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValuationReportServiceImpl implements ValuationReportService {

    private final ValuationReportRepository valuationReportRepository;
    private final ProjectRepository projectRepository;
    private final ReportSealService reportSealService;

    @Override
    public List<ValuationReportDto> listByProjectId(Long projectId) {
        return valuationReportRepository.findByProjectId(projectId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ValuationReportDto getById(Long id) {
        return toDto(valuationReportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("估价报告不存在")));
    }

    @Override
    public ValuationReportDto getLatestDownloadableByProjectId(Long projectId) {
        return valuationReportRepository.findDownloadableByProjectId(projectId, PageRequest.of(0, 1)).stream()
                .findFirst()
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException("暂无可下载报告，请先生成报告"));
    }

    @Override
    @Transactional
    public ValuationReportDto create(Long projectId, ValuationReportDto dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException("项目不存在"));
        ValuationReport r = toEntity(dto, projectId);
        applyProjectDefaults(r, project);
        r = valuationReportRepository.save(r);
        reportSealService.createEmptySeal(r.getId(), projectId);
        return toDto(r);
    }

    @Override
    public ValuationReportDto update(Long id, ValuationReportDto dto) {
        ValuationReport r = valuationReportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("估价报告不存在"));
        validateReportCode(dto.getReportCode(), id);
        applyDto(r, dto);
        return toDto(valuationReportRepository.save(r));
    }

    @Override
    public boolean isReportCodeAvailable(String reportCode, Long excludeId) {
        if (!StringUtils.hasText(reportCode)) {
            return false;
        }
        String code = reportCode.trim();
        if (excludeId != null) {
            return !valuationReportRepository.existsByReportCodeAndIdNot(code, excludeId);
        }
        return !valuationReportRepository.existsByReportCode(code);
    }

    @Override
    public void delete(Long id) {
        valuationReportRepository.deleteById(id);
    }

    @Override
    public Page<ReportListDto> searchReports(String projectCode, String address, Pageable pageable) {
        String codeKw = StringUtils.hasText(projectCode) ? "%" + projectCode + "%" : null;
        String addrKw = StringUtils.hasText(address) ? "%" + address + "%" : null;
        return valuationReportRepository.searchReports(codeKw, addrKw, pageable)
                .map(this::withPublicReportUrl);
    }

    private ValuationReportDto toDto(ValuationReport r) {
        ValuationReportDto dto = new ValuationReportDto();
        dto.setId(r.getId());
        dto.setReportCode(r.getReportCode());
        dto.setStartTime(r.getStartTime());
        dto.setEndTime(r.getEndTime());
        dto.setUnitPrice(r.getUnitPrice());
        dto.setTotalPrice(r.getTotalPrice());
        dto.setMortgageValue(r.getMortgageValue());
        dto.setPriorityCompensationAmount(r.getPriorityCompensationAmount());
        dto.setPriorityCompensationDescription(r.getPriorityCompensationDescription());
        dto.setValueDate(r.getValueDate());
        dto.setReportIssueDate(r.getReportIssueDate());
        dto.setValidStartDate(r.getValidStartDate());
        dto.setValidEndDate(r.getValidEndDate());
        dto.setValuer1Name(r.getValuer1Name());
        dto.setValuer1CertNo(r.getValuer1CertNo());
        dto.setValuer2Name(r.getValuer2Name());
        dto.setValuer2CertNo(r.getValuer2CertNo());
        dto.setValuationResult(r.getValuationResult());
        dto.setAreaEvaluation(r.getAreaEvaluation());
        dto.setSurroundingTransactions(r.getSurroundingTransactions());
        dto.setLiquidityAnalysis(r.getLiquidityAnalysis());
        dto.setFloorPlan(r.getFloorPlan());
        dto.setLandGrantDeduction(r.getLandGrantDeduction());
        dto.setDecorationNewRate(r.getDecorationNewRate());
        dto.setEquipmentNewRate(r.getEquipmentNewRate());
        dto.setReportUrl(toPublicReportUrl(r.getReportUrl()));
        dto.setBankSuggestion(r.getBankSuggestion());
        dto.setLandPlot(r.getLandPlot());
        return dto;
    }

    private ReportListDto withPublicReportUrl(ReportListDto dto) {
        dto.setReportUrl(toPublicReportUrl(dto.getReportUrl()));
        return dto;
    }

    private ValuationReport toEntity(ValuationReportDto dto, Long projectId) {
        ValuationReport r = new ValuationReport();
        r.setProjectId(projectId);
        applyDto(r, dto);
        return r;
    }

    private void applyProjectDefaults(ValuationReport r, Project project) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        if (r.getStartTime() == null) {
            r.setStartTime(now);
        }
        if (r.getUnitPrice() == null) {
            r.setUnitPrice(project.getValuationUnitPrice());
        }
        if (r.getTotalPrice() == null) {
            r.setTotalPrice(project.getValuationTotalPrice());
        }
        if (r.getMortgageValue() == null) {
            r.setMortgageValue(project.getValuationTotalPrice());
        }
        if (r.getValueDate() == null) {
            r.setValueDate(project.getValuationTime());
        }
        if (r.getReportIssueDate() == null) {
            r.setReportIssueDate(today);
        }
        if (r.getValidStartDate() == null) {
            r.setValidStartDate(today);
        }
        if (r.getValidEndDate() == null) {
            r.setValidEndDate(today.plusYears(1).minusDays(1));
        }
    }

    private void validateReportCode(String reportCode, Long excludeId) {
        if (!StringUtils.hasText(reportCode)) {
            throw new BusinessException("报告编号不能为空");
        }
        if (!isReportCodeAvailable(reportCode, excludeId)) {
            throw new BusinessException("报告编号不可用");
        }
    }

    private void applyDto(ValuationReport r, ValuationReportDto dto) {
        if (StringUtils.hasText(dto.getReportCode())) {
            r.setReportCode(dto.getReportCode().trim());
        }
        r.setStartTime(dto.getStartTime());
        r.setEndTime(dto.getEndTime());
        r.setUnitPrice(dto.getUnitPrice());
        r.setTotalPrice(dto.getTotalPrice());
        r.setMortgageValue(dto.getMortgageValue());
        r.setPriorityCompensationAmount(dto.getPriorityCompensationAmount());
        r.setPriorityCompensationDescription(dto.getPriorityCompensationDescription());
        r.setValueDate(dto.getValueDate());
        r.setReportIssueDate(dto.getReportIssueDate());
        r.setValidStartDate(dto.getValidStartDate());
        r.setValidEndDate(dto.getValidEndDate());
        r.setValuer1Name(dto.getValuer1Name());
        r.setValuer1CertNo(dto.getValuer1CertNo());
        r.setValuer2Name(dto.getValuer2Name());
        r.setValuer2CertNo(dto.getValuer2CertNo());
        r.setValuationResult(dto.getValuationResult());
        r.setAreaEvaluation(dto.getAreaEvaluation());
        r.setSurroundingTransactions(dto.getSurroundingTransactions());
        r.setLiquidityAnalysis(dto.getLiquidityAnalysis());
        r.setFloorPlan(dto.getFloorPlan());
        r.setLandGrantDeduction(dto.getLandGrantDeduction());
        r.setDecorationNewRate(dto.getDecorationNewRate());
        r.setEquipmentNewRate(dto.getEquipmentNewRate());
        r.setReportUrl(toStoredReportUrl(dto.getReportUrl()));
        r.setBankSuggestion(dto.getBankSuggestion());
        r.setLandPlot(dto.getLandPlot());
    }

    private String toPublicReportUrl(String reportUrl) {
        if (!StringUtils.hasText(reportUrl)) {
            return reportUrl;
        }
        String value = reportUrl.trim();
        String uploadsMarker = "/uploads/";
        int uploadsIndex = value.indexOf(uploadsMarker);
        if (uploadsIndex >= 0) {
            return value.substring(uploadsIndex);
        }
        if (value.startsWith("uploads/")) {
            return "/" + value;
        }
        return value.startsWith("/uploads/")
                ? value
                : "/uploads/" + trimLeadingSlash(value);
    }

    private String toStoredReportUrl(String reportUrl) {
        if (!StringUtils.hasText(reportUrl)) {
            return reportUrl;
        }
        String value = reportUrl.trim();
        String uploadsMarker = "/uploads/";
        int uploadsIndex = value.indexOf(uploadsMarker);
        if (uploadsIndex >= 0) {
            return value.substring(uploadsIndex + uploadsMarker.length());
        }
        if (value.startsWith("uploads/")) {
            return value.substring("uploads/".length());
        }
        return value;
    }

    private String trimLeadingSlash(String value) {
        String result = value;
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        return result;
    }
}
