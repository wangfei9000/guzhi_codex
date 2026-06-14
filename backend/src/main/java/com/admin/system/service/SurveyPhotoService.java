package com.admin.system.service;

import com.admin.system.dto.SurveyPhotoDto;
import java.util.List;

public interface SurveyPhotoService {
    List<SurveyPhotoDto> listBySurveyId(Long surveyId);
    SurveyPhotoDto getById(Long id);
    SurveyPhotoDto create(Long projectId, Long surveyId, SurveyPhotoDto dto);
    SurveyPhotoDto update(Long id, SurveyPhotoDto dto);
    void delete(Long id);
}
