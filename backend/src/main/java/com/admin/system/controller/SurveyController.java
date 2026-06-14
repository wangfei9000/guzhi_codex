package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.SurveyDto;
import com.admin.system.dto.SurveyListDto;
import com.admin.system.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/by-project/{projectId}")
    public ApiResponse<List<SurveyDto>> listByProjectId(@PathVariable Long projectId) {
        return ApiResponse.success(surveyService.listByProjectId(projectId));
    }

    @PostMapping
    public ApiResponse<SurveyDto> create(@RequestParam Long projectId,
                                          @RequestBody SurveyDto dto) {
        return ApiResponse.success("创建成功", surveyService.create(projectId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<SurveyDto> update(@PathVariable Long id,
                                          @RequestBody SurveyDto dto) {
        return ApiResponse.success("更新成功", surveyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        surveyService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/by-code/{surveyCode}")
    public ApiResponse<SurveyDto> getBySurveyCode(@PathVariable String surveyCode) {
        System.out.println("======> request /by-code/{"+surveyCode+"}");
        return ApiResponse.success(surveyService.getBySurveyCode(surveyCode));
    }

    @GetMapping("/by-short-code/{code}")
    public ApiResponse<SurveyDto> getByShortCode(@PathVariable String code) {
        System.out.println("======> request /by-short-code/{"+code+"}");
        return ApiResponse.success(surveyService.getByCode(code));
    }

    @GetMapping("/surveys")
    public ApiResponse<PageResponse<SurveyListDto>> listSurveys(
            @RequestParam(required = false) String projectCode,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<SurveyListDto> page = surveyService.searchSurveys(projectCode, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }
}
