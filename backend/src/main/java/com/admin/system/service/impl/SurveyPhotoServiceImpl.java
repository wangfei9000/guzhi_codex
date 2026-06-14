package com.admin.system.service.impl;

import com.admin.system.dto.SurveyPhotoDto;
import com.admin.system.entity.SurveyPhoto;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SurveyPhotoRepository;
import com.admin.system.service.SurveyPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyPhotoServiceImpl implements SurveyPhotoService {

    private final SurveyPhotoRepository surveyPhotoRepository;

    @Override
    public List<SurveyPhotoDto> listBySurveyId(Long surveyId) {
        return surveyPhotoRepository.findBySurveyId(surveyId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public SurveyPhotoDto getById(Long id) {
        return toDto(surveyPhotoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("照片不存在")));
    }

    @Override
    public SurveyPhotoDto create(Long projectId, Long surveyId, SurveyPhotoDto dto) {
        SurveyPhoto p = toEntity(dto, projectId, surveyId);
        if (p.getPhotoCode() == null || p.getPhotoCode().isBlank()) {
            p.setPhotoCode("PHOTO-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));
        }
        return toDto(surveyPhotoRepository.save(p));
    }

    @Override
    public SurveyPhotoDto update(Long id, SurveyPhotoDto dto) {
        SurveyPhoto p = surveyPhotoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("照片不存在"));
        applyDto(p, dto);
        return toDto(surveyPhotoRepository.save(p));
    }

    @Override
    public void delete(Long id) {
        surveyPhotoRepository.deleteById(id);
    }

    private SurveyPhotoDto toDto(SurveyPhoto p) {
        SurveyPhotoDto dto = new SurveyPhotoDto();
        dto.setId(p.getId());
        dto.setSurveyId(p.getSurveyId());
        dto.setPhotoCode(p.getPhotoCode());
        dto.setPhotoPath(p.getPhotoPath());
        dto.setPhotoCategory(p.getPhotoCategory());
        dto.setPhotoDescription(p.getPhotoDescription());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    private SurveyPhoto toEntity(SurveyPhotoDto dto, Long projectId, Long surveyId) {
        SurveyPhoto p = new SurveyPhoto();
        p.setProjectId(projectId);
        p.setSurveyId(surveyId);
        applyDto(p, dto);
        return p;
    }

    private void applyDto(SurveyPhoto p, SurveyPhotoDto dto) {
        if (dto.getPhotoCode() != null) p.setPhotoCode(dto.getPhotoCode());
        if (dto.getPhotoPath() != null) p.setPhotoPath(dto.getPhotoPath());
        if (dto.getPhotoCategory() != null) p.setPhotoCategory(dto.getPhotoCategory());
        if (dto.getPhotoDescription() != null) p.setPhotoDescription(dto.getPhotoDescription());
    }
}
