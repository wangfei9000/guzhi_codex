package com.admin.system.service.impl;

import com.admin.system.dto.ScheduleDto;
import com.admin.system.entity.SysSchedule;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SysScheduleRepository;
import com.admin.system.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final SysScheduleRepository scheduleRepository;

    @Override
    public Page<ScheduleDto> listSchedules(String keyword, Pageable pageable) {
        String kw = StringUtils.hasText(keyword) ? "%" + keyword.toLowerCase() + "%" : null;
        return scheduleRepository.search(kw, pageable).map(this::toDto);
    }

    @Override
    public ScheduleDto getScheduleById(Long id) {
        SysSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("调度记录不存在"));
        return toDto(schedule);
    }

    private ScheduleDto toDto(SysSchedule s) {
        ScheduleDto dto = new ScheduleDto();
        dto.setId(s.getId());
        dto.setRegistrationDate(s.getRegistrationDate());
        dto.setCode(s.getCode());
        dto.setReportNo(s.getReportNo());
        dto.setOrderTaker(s.getOrderTaker());
        dto.setAgency(s.getAgency());
        dto.setReporter(s.getReporter());
        dto.setReporterPhone(s.getReporterPhone());
        dto.setContact(s.getContact());
        dto.setContactPhone(s.getContactPhone());
        dto.setCustomerService(s.getCustomerService());
        dto.setProjectAddress(s.getProjectAddress());
        dto.setSurveyor(s.getSurveyor());
        dto.setAppraiser(s.getAppraiser());
        dto.setStatus(s.getStatus());
        dto.setUnitPrice(s.getUnitPrice());
        dto.setTotalPrice(s.getTotalPrice());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}
