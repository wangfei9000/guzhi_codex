package com.admin.system.service.impl;

import com.admin.system.dto.SurveyListDto;
import com.admin.system.dto.SurveyDto;
import com.admin.system.dto.SurveyPhotoDto;
import com.admin.system.entity.Survey;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SurveyPhotoRepository;
import com.admin.system.repository.SurveyRepository;
import com.admin.system.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyPhotoRepository surveyPhotoRepository;

    @Override
    public List<SurveyDto> listByProjectId(Long projectId) {
        return surveyRepository.findByProjectId(projectId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public SurveyDto getById(Long id) {
        return toDto(surveyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("勘查记录不存在")));
    }

    @Override
    public SurveyDto create(Long projectId, SurveyDto dto) {
        Survey s = toEntity(dto, projectId);
        if (s.getSurveyCode() == null || s.getSurveyCode().isBlank()) {
            s.setSurveyCode("SUR-" + System.currentTimeMillis() );// + "-" + UUID.randomUUID().toString().substring(0, 8));
        }
        s.setCode(generateShortCode());
        s.setSurveyStatus("未查勘");
        return toDto(surveyRepository.save(s));
    }

    @Override
    public SurveyDto update(Long id, SurveyDto dto) {
        Survey s = surveyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("勘查记录不存在"));
        applyDto(s, dto);
        return toDto(surveyRepository.save(s));
    }

    @Override
    public SurveyDto getBySurveyCode(String surveyCode) {
        Survey s = surveyRepository.findBySurveyCode(surveyCode)
                .orElseThrow(() -> new BusinessException("勘查记录不存在，编号: " + surveyCode));
        SurveyDto dto = toDto(s);
        // Also load photos
        List<SurveyPhotoDto> photos = surveyPhotoRepository.findBySurveyId(s.getId())
                .stream().map(p -> {
                    SurveyPhotoDto pd = new SurveyPhotoDto();
                    pd.setId(p.getId());
                    pd.setSurveyId(p.getSurveyId());
                    pd.setPhotoCode(p.getPhotoCode());
                    pd.setPhotoPath(p.getPhotoPath());
                    pd.setPhotoCategory(p.getPhotoCategory());
                    pd.setPhotoDescription(p.getPhotoDescription());
                    pd.setCreatedAt(p.getCreatedAt());
                    return pd;
                }).collect(Collectors.toList());
        dto.setPhotos(photos);
        return dto;
    }

    @Override
    public SurveyDto getByCode(String code) {
        Survey s = surveyRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("勘查记录不存在，编号: " + code));
        SurveyDto dto = toDto(s);
        List<SurveyPhotoDto> photos = surveyPhotoRepository.findBySurveyId(s.getId())
                .stream().map(p -> {
                    SurveyPhotoDto pd = new SurveyPhotoDto();
                    pd.setId(p.getId());
                    pd.setSurveyId(p.getSurveyId());
                    pd.setPhotoCode(p.getPhotoCode());
                    pd.setPhotoPath(p.getPhotoPath());
                    pd.setPhotoCategory(p.getPhotoCategory());
                    pd.setPhotoDescription(p.getPhotoDescription());
                    pd.setCreatedAt(p.getCreatedAt());
                    return pd;
                }).collect(Collectors.toList());
        dto.setPhotos(photos);
        return dto;
    }

    @Override
    public void delete(Long id) {
        surveyRepository.deleteById(id);
    }

    @Override
    public Page<SurveyListDto> searchSurveys(String projectCode, Pageable pageable) {
        String codeKw = StringUtils.hasText(projectCode) ? "%" + projectCode + "%" : null;
        return surveyRepository.searchSurveys(codeKw, pageable);
    }

    private SurveyDto toDto(Survey s) {
        SurveyDto dto = new SurveyDto();
        dto.setId(s.getId());
        dto.setSurveyCode(s.getSurveyCode());
        dto.setSurveyor(s.getSurveyor());
        dto.setReceptionist(s.getReceptionist());
        dto.setReceptionistPhone(s.getReceptionistPhone());
        dto.setSurveyDate(s.getSurveyDate());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setPropertyCertVerified(s.getPropertyCertVerified());
        dto.setOwnershipDispute(s.getOwnershipDispute());
        dto.setRemark(s.getRemark());
        dto.setProjectId(s.getProjectId());
        dto.setCode(s.getCode());
        dto.setSurveyStatus(s.getSurveyStatus());
        return dto;
    }

    //private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String CODE_CHARS = "abcdefghkmnprstuvwxyz12345789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    private Survey toEntity(SurveyDto dto, Long projectId) {
        Survey s = new Survey();
        s.setProjectId(projectId);
        applyDto(s, dto);
        return s;
    }

    private void applyDto(Survey s, SurveyDto dto) {
        if (dto.getSurveyCode() != null) s.setSurveyCode(dto.getSurveyCode());
        if (dto.getSurveyor() != null) s.setSurveyor(dto.getSurveyor());
        if (dto.getReceptionist() != null) s.setReceptionist(dto.getReceptionist());
        if (dto.getReceptionistPhone() != null) s.setReceptionistPhone(dto.getReceptionistPhone());
        if (dto.getSurveyDate() != null) s.setSurveyDate(dto.getSurveyDate());
        if (dto.getStartTime() != null) s.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) s.setEndTime(dto.getEndTime());
        if (dto.getPropertyCertVerified() != null) s.setPropertyCertVerified(dto.getPropertyCertVerified());
        if (dto.getOwnershipDispute() != null) s.setOwnershipDispute(dto.getOwnershipDispute());
        if (dto.getRemark() != null) s.setRemark(dto.getRemark());
        if (dto.getSurveyStatus() != null) s.setSurveyStatus(dto.getSurveyStatus());
    }
}
