package com.admin.system.service;

import com.admin.system.dto.OwnershipInfoDto;

public interface OwnershipInfoService {
    /**
     * 根据项目ID获取权属信息（单个）
     */
    OwnershipInfoDto getByProjectId(Long projectId);

    /**
     * 保存权属信息（新增或更新）
     */
    OwnershipInfoDto save(Long projectId, OwnershipInfoDto dto);
}
