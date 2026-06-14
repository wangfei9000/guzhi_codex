package com.admin.system.service;

import com.admin.system.dto.CreatePermissionRequest;
import com.admin.system.dto.PermissionDto;
import java.util.List;

public interface PermissionService {
    List<PermissionDto> getPermissionTree();
    PermissionDto createPermission(CreatePermissionRequest request);
    PermissionDto updatePermission(Long id, CreatePermissionRequest request);
    void deletePermission(Long id);
}
