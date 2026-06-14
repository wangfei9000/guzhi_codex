package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.CreatePermissionRequest;
import com.admin.system.dto.PermissionDto;
import com.admin.system.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/tree")
    public ApiResponse<List<PermissionDto>> getPermissionTree() {
        return ApiResponse.success(permissionService.getPermissionTree());
    }

    @PostMapping
    public ApiResponse<PermissionDto> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        return ApiResponse.success("创建成功", permissionService.createPermission(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionDto> updatePermission(@PathVariable Long id,
                                                        @Valid @RequestBody CreatePermissionRequest request) {
        return ApiResponse.success("更新成功", permissionService.updatePermission(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success("删除成功", null);
    }
}
