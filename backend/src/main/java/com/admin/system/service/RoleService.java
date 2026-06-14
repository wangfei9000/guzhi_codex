package com.admin.system.service;

import com.admin.system.dto.CreateRoleRequest;
import com.admin.system.dto.RoleDto;
import java.util.List;
import java.util.Set;

public interface RoleService {
    List<RoleDto> listRoles();
    RoleDto getRoleById(Long id);
    RoleDto createRole(CreateRoleRequest request);
    RoleDto updateRole(Long id, CreateRoleRequest request);
    void deleteRole(Long id);
    void assignPermissions(Long roleId, Set<Long> permissionIds);
}
