package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.CreateRoleRequest;
import com.admin.system.dto.RoleDto;
import com.admin.system.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleDto>> listRoles() {
        return ApiResponse.success(roleService.listRoles());
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleDto> getRoleById(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRoleById(id));
    }

    @PostMapping
    public ApiResponse<RoleDto> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ApiResponse.success("创建成功", roleService.createRole(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleDto> updateRole(@PathVariable Long id, @Valid @RequestBody CreateRoleRequest request) {
        return ApiResponse.success("更新成功", roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success("删除成功", null);
    }

    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> assignPermissions(@PathVariable Long id, @RequestBody Set<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return ApiResponse.success("权限分配成功", null);
    }
}
