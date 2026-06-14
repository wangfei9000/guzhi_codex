package com.admin.system.service.impl;

import com.admin.system.dto.ReportReviewDto;
import com.admin.system.entity.ReportReview;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.ReportReviewRepository;
import com.admin.system.service.ReportReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportReviewServiceImpl implements ReportReviewService {

    private final ReportReviewRepository reportReviewRepository;

    @Override
    public List<ReportReviewDto> listByReportId(Long reportId) {
        return reportReviewRepository.findByReportId(reportId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ReportReviewDto getById(Long id) {
        return toDto(reportReviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审核记录不存在")));
    }

    @Override
    public ReportReviewDto create(Long projectId, Long reportId, ReportReviewDto dto) {
        ReportReview r = toEntity(dto, projectId, reportId);
        return toDto(reportReviewRepository.save(r));
    }

    @Override
    public ReportReviewDto update(Long id, ReportReviewDto dto) {
        ReportReview r = reportReviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审核记录不存在"));
        applyDto(r, dto);
        return toDto(reportReviewRepository.save(r));
    }

    @Override
    public void delete(Long id) {
        reportReviewRepository.deleteById(id);
    }

    private ReportReviewDto toDto(ReportReview r) {
        ReportReviewDto dto = new ReportReviewDto();
        dto.setId(r.getId());
        dto.setReportId(r.getReportId());
        dto.setReviewer(r.getReviewer());
        dto.setReviewDate(r.getReviewDate());
        dto.setReviewOpinion(r.getReviewOpinion());
        dto.setReviewResult(r.getReviewResult());
        return dto;
    }

    private ReportReview toEntity(ReportReviewDto dto, Long projectId, Long reportId) {
        ReportReview r = new ReportReview();
        r.setProjectId(projectId);
        r.setReportId(reportId);
        applyDto(r, dto);
        return r;
    }

    private void applyDto(ReportReview r, ReportReviewDto dto) {
        r.setReviewer(dto.getReviewer());
        r.setReviewDate(dto.getReviewDate());
        r.setReviewOpinion(dto.getReviewOpinion());
        r.setReviewResult(dto.getReviewResult());
    }
}
