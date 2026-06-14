package com.admin.system.service;

import com.admin.system.dto.ScheduleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleService {
    Page<ScheduleDto> listSchedules(String keyword, Pageable pageable);
    ScheduleDto getScheduleById(Long id);
}
