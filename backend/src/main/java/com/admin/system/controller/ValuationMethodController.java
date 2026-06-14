package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.ValuationMethodDto;
import com.admin.system.service.ValuationMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/valuation-method")
@RequiredArgsConstructor
public class ValuationMethodController {

    private final ValuationMethodService valuationMethodService;

    @GetMapping("/by-report/{reportId}")
    public ApiResponse<List<ValuationMethodDto>> listByReportId(@PathVariable Long reportId) {
        return ApiResponse.success(valuationMethodService.listByReportId(reportId));
    }

    @PostMapping
    public ApiResponse<ValuationMethodDto> create(@RequestParam Long reportId,
                                                   @RequestBody ValuationMethodDto dto) {
        return ApiResponse.success("创建成功", valuationMethodService.create(reportId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<ValuationMethodDto> update(@PathVariable Long id,
                                                   @RequestBody ValuationMethodDto dto) {
        return ApiResponse.success("更新成功", valuationMethodService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        valuationMethodService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
