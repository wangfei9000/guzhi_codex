package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.SurveyPhotoDto;
import com.admin.system.service.SurveyPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey-photo")
@RequiredArgsConstructor
public class SurveyPhotoController {

    private final SurveyPhotoService surveyPhotoService;

    @GetMapping("/by-survey/{surveyId}")
    public ApiResponse<List<SurveyPhotoDto>> listBySurveyId(@PathVariable Long surveyId) {
        return ApiResponse.success(surveyPhotoService.listBySurveyId(surveyId));
    }

    @PostMapping
    public ApiResponse<SurveyPhotoDto> create(@RequestParam Long projectId,
                                               @RequestParam Long surveyId,
                                               @RequestBody SurveyPhotoDto dto) {
        return ApiResponse.success("创建成功", surveyPhotoService.create(projectId, surveyId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<SurveyPhotoDto> update(@PathVariable Long id,
                                               @RequestBody SurveyPhotoDto dto) {
        return ApiResponse.success("更新成功", surveyPhotoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        surveyPhotoService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
