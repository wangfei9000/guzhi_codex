package com.admin.system.service.impl;

import com.admin.system.dto.ReportTemplateDto;
import com.admin.system.entity.ReportTemplate;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.ReportTemplateRepository;
import com.admin.system.service.ReportTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ReportTemplateServiceImpl implements ReportTemplateService {

    private final ReportTemplateRepository reportTemplateRepository;

    @Override
    public Page<ReportTemplateDto> list(Pageable pageable) {
        return reportTemplateRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public ReportTemplateDto create(ReportTemplateDto dto) {
        ReportTemplate template = new ReportTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setTemplateContent(dto.getTemplateContent());
        template.setStatus(StringUtils.hasText(dto.getStatus()) ? dto.getStatus() : "启用");
        return toDto(reportTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public ReportTemplateDto update(Long id, ReportTemplateDto dto) {
        ReportTemplate template = reportTemplateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("报告模版不存在"));
        if (!StringUtils.hasText(dto.getTemplateContent())) {
            throw new BusinessException("模版内容不能为空");
        }
        template.setTemplateContent(dto.getTemplateContent());
        if (StringUtils.hasText(dto.getTemplateName())) {
            template.setTemplateName(dto.getTemplateName());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            template.setStatus(dto.getStatus());
        }
        return toDto(reportTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reportTemplateRepository.existsById(id)) {
            throw new BusinessException("报告模版不存在");
        }
        reportTemplateRepository.deleteById(id);
    }

    private ReportTemplateDto toDto(ReportTemplate template) {
        ReportTemplateDto dto = new ReportTemplateDto();
        dto.setId(template.getId());
        dto.setTemplateName(template.getTemplateName());
        dto.setTemplateContent(template.getTemplateContent());
        dto.setStatus(template.getStatus());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());
        return dto;
    }
}
