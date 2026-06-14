package com.admin.system.service;

import com.admin.system.dto.ValuationMethodDto;
import java.util.List;

public interface ValuationMethodService {
    List<ValuationMethodDto> listByReportId(Long reportId);
    ValuationMethodDto getById(Long id);
    ValuationMethodDto create(Long reportId, ValuationMethodDto dto);
    ValuationMethodDto update(Long id, ValuationMethodDto dto);
    void delete(Long id);
}
