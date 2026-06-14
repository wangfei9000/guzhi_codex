package com.admin.system.service.impl;

import com.admin.system.dto.ValuationMethodDto;
import com.admin.system.entity.ValuationMethod;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.ValuationMethodRepository;
import com.admin.system.service.ValuationMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValuationMethodServiceImpl implements ValuationMethodService {

    private final ValuationMethodRepository valuationMethodRepository;

    @Override
    public List<ValuationMethodDto> listByReportId(Long reportId) {
        return valuationMethodRepository.findByReportId(reportId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ValuationMethodDto getById(Long id) {
        return toDto(valuationMethodRepository.findById(id)
                .orElseThrow(() -> new BusinessException("估价方法不存在")));
    }

    @Override
    public ValuationMethodDto create(Long reportId, ValuationMethodDto dto) {
        ValuationMethod m = toEntity(dto, reportId);
        return toDto(valuationMethodRepository.save(m));
    }

    @Override
    public ValuationMethodDto update(Long id, ValuationMethodDto dto) {
        ValuationMethod m = valuationMethodRepository.findById(id)
                .orElseThrow(() -> new BusinessException("估价方法不存在"));
        applyDto(m, dto);
        return toDto(valuationMethodRepository.save(m));
    }

    @Override
    public void delete(Long id) {
        valuationMethodRepository.deleteById(id);
    }

    private ValuationMethodDto toDto(ValuationMethod m) {
        ValuationMethodDto dto = new ValuationMethodDto();
        dto.setId(m.getId());
        dto.setMethodCode(m.getMethodCode());
        dto.setMethodName(m.getMethodName());
        dto.setWeight(m.getWeight());
        dto.setUnitPrice(m.getUnitPrice());
        dto.setAppraiserSignature(m.getAppraiserSignature());
        dto.setDescription(m.getDescription());
        dto.setReportId(m.getReportId());
        return dto;
    }

    private ValuationMethod toEntity(ValuationMethodDto dto, Long reportId) {
        ValuationMethod m = new ValuationMethod();
        m.setReportId(reportId);
        applyDto(m, dto);
        return m;
    }

    private void applyDto(ValuationMethod m, ValuationMethodDto dto) {
        m.setMethodCode(dto.getMethodCode());
        m.setMethodName(dto.getMethodName());
        m.setWeight(dto.getWeight());
        m.setUnitPrice(dto.getUnitPrice());
        m.setAppraiserSignature(dto.getAppraiserSignature());
        m.setDescription(dto.getDescription());
    }
}
