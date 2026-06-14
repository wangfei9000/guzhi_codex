package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.ReconciliationRequest;
import com.admin.system.dto.UserDto;
import com.admin.system.entity.ReconciliationRecord;
import com.admin.system.exception.BusinessException;
import com.admin.system.service.ReconciliationService;
import com.admin.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {

    private final ReconciliationService reconciliationService;
    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponse<ReconciliationRecord>> listReconciliations(
            Principal principal,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTime,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTime,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        UserDto currentUser = userService.getCurrentUser(principal.getName());
        Long organizationId = currentUser.getOrganizationId();
        if (organizationId == null) {
            throw new BusinessException("当前用户未设置所属机构，无法查看对账列表");
        }

        return ApiResponse.success(PageResponse.of(
                reconciliationService.listReconciliations(organizationId, startTime, endTime, pageable)));
    }

    @PostMapping
    public ApiResponse<ReconciliationRecord> startReconciliation(@RequestBody ReconciliationRequest request,
                                                                  Principal principal) {
        UserDto currentUser = userService.getCurrentUser(principal.getName());
        Long organizationId = currentUser.getOrganizationId();
        String organizationName = currentUser.getOrganizationName();
        if (organizationId == null || organizationName == null || organizationName.isBlank()) {
            throw new BusinessException("当前用户未设置所属机构，无法对账");
        }

        ReconciliationRecord record = reconciliationService.startReconciliation(request, organizationId, organizationName);
        return ApiResponse.success("对账成功", record);
    }
}
