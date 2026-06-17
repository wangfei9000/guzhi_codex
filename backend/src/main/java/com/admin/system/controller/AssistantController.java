package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.AssistantGenerateRequest;
import com.admin.system.dto.AssistantGenerateResponse;
import com.admin.system.service.AssistantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/generate")
    public ApiResponse<AssistantGenerateResponse> generate(@Valid @RequestBody AssistantGenerateRequest request) {
        return ApiResponse.success(assistantService.generate(request));
    }
}
