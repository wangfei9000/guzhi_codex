package com.admin.system.service;

import com.admin.system.dto.SurveyDto;
import com.admin.system.dto.SurveyListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyService {
    List<SurveyDto> listByProjectId(Long projectId);
    SurveyDto getById(Long id);
    SurveyDto create(Long projectId, SurveyDto dto);
    SurveyDto update(Long id, SurveyDto dto);
    void delete(Long id);
    Page<SurveyListDto> searchSurveys(String projectCode, Pageable pageable);
    SurveyDto getBySurveyCode(String surveyCode);
    SurveyDto getByCode(String code);
}
