package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.RevaluationRequest;
import com.admin.system.dto.UserDto;
import com.admin.system.entity.RevaluationRecord;
import com.admin.system.exception.BusinessException;
import com.admin.system.service.RevaluationService;
import com.admin.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/revaluation")
@RequiredArgsConstructor
public class RevaluationController {

    private final RevaluationService revaluationService;
    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponse<RevaluationRecord>> listRevaluations(
            Principal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        UserDto currentUser = userService.getCurrentUser(principal.getName());
        Long organizationId = currentUser.getOrganizationId();
        if (organizationId == null) {
            throw new BusinessException("当前用户未设置所属机构，无法查看复估列表");
        }

        return ApiResponse.success(PageResponse.of(revaluationService.listRevaluations(organizationId, pageable)));
    }

    @PostMapping
    public ApiResponse<RevaluationRecord> startRevaluation(@RequestBody RevaluationRequest request,
                                                            Principal principal) {
        UserDto currentUser = userService.getCurrentUser(principal.getName());
        String organizationName = currentUser.getOrganizationName();
        Long organizationId = currentUser.getOrganizationId();
        if (organizationId == null || organizationName == null || organizationName.isBlank()) {
            throw new BusinessException("当前用户未设置所属机构，无法复估");
        }

        RevaluationRecord record = revaluationService.startRevaluation(request, organizationId, organizationName);
        return ApiResponse.success("复估成功", record);
    }
}
