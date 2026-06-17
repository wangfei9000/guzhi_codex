package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssistantGenerateRequest {

    @NotBlank(message = "请输入内容")
    private String prompt;
}
