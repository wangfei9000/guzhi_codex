package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.ScheduleDto;
import com.admin.system.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ApiResponse<PageResponse<ScheduleDto>> listSchedules(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "registrationDate") Pageable pageable) {
        Page<ScheduleDto> page = scheduleService.listSchedules(keyword, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    public ApiResponse<ScheduleDto> getScheduleById(@PathVariable Long id) {
        ScheduleDto schedule = scheduleService.getScheduleById(id);
        return ApiResponse.success(schedule);
    }
}
