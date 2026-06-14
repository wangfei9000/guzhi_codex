package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.OwnershipInfoDto;
import com.admin.system.service.OwnershipInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ownership-info")
@RequiredArgsConstructor
public class OwnershipInfoController {

    private final OwnershipInfoService ownershipInfoService;

    /**
     * 根据项目ID获取权属信息（单个）
     */
    @GetMapping("/by-project/{projectId}")
    public ApiResponse<OwnershipInfoDto> getByProjectId(@PathVariable Long projectId) {
        return ApiResponse.success(ownershipInfoService.getByProjectId(projectId));
    }

    /**
     * 保存权属信息（新增或更新）
     */
    @PostMapping("/save/{projectId}")
    public ApiResponse<OwnershipInfoDto> save(@PathVariable Long projectId,
                                               @RequestBody OwnershipInfoDto dto) {
        return ApiResponse.success("保存成功", ownershipInfoService.save(projectId, dto));
    }
}
