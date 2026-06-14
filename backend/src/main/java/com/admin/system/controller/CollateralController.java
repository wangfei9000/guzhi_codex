package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.CollateralDto;
import com.admin.system.service.CollateralService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collateral")
@RequiredArgsConstructor
public class CollateralController {

    private final CollateralService collateralService;

    @GetMapping("/by-project/{projectId}")
    public ApiResponse<List<CollateralDto>> listByProjectId(@PathVariable Long projectId) {
        return ApiResponse.success(collateralService.listByProjectId(projectId));
    }

    @PostMapping
    public ApiResponse<CollateralDto> create(@RequestParam Long projectId,
                                              @RequestBody CollateralDto dto) {
        return ApiResponse.success("创建成功", collateralService.create(projectId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<CollateralDto> update(@PathVariable Long id,
                                              @RequestBody CollateralDto dto) {
        return ApiResponse.success("更新成功", collateralService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        collateralService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
