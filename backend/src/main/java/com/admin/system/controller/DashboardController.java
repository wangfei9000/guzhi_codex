package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.DashboardStatsDto;
import com.admin.system.entity.SysUser;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SysUserRepository userRepository;

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsDto> getStats(Principal principal) {
        Long userId = resolveUserId(principal);
        return ApiResponse.success(dashboardService.getStats(userId));
    }

    private Long resolveUserId(Principal principal) {
        if (principal == null) return 1L;
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .map(SysUser::getId)
                .orElse(1L);
    }
}
