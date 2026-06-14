package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.OrganizationDto;
import com.admin.system.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public ApiResponse<PageResponse<OrganizationDto>> listOrganizations(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<OrganizationDto> page = organizationService.listOrganizations(pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/options")
    public ApiResponse<List<OrganizationDto>> listOptions() {
        return ApiResponse.success(organizationService.listOptions());
    }

    @GetMapping("/{id}")
    public ApiResponse<OrganizationDto> getOrganizationById(@PathVariable Long id) {
        return ApiResponse.success(organizationService.getOrganizationById(id));
    }

    @PostMapping
    public ApiResponse<OrganizationDto> createOrganization(@Valid @RequestBody OrganizationDto request) {
        return ApiResponse.success("创建成功", organizationService.createOrganization(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<OrganizationDto> updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationDto request) {
        return ApiResponse.success("更新成功", organizationService.updateOrganization(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ApiResponse.success("删除成功", null);
    }
}
