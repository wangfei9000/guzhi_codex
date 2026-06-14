package com.admin.system.service;

import com.admin.system.dto.CollateralDto;
import java.util.List;

public interface CollateralService {
    List<CollateralDto> listByProjectId(Long projectId);
    CollateralDto getById(Long id);
    CollateralDto create(Long projectId, CollateralDto dto);
    CollateralDto update(Long id, CollateralDto dto);
    void delete(Long id);
}
