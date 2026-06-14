package com.admin.system.service;

import com.admin.system.dto.ReportTemplateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportTemplateService {
    Page<ReportTemplateDto> list(Pageable pageable);
    ReportTemplateDto create(ReportTemplateDto dto);
    ReportTemplateDto update(Long id, ReportTemplateDto dto);
    void delete(Long id);
}
