package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportTemplateDto {
    private Long id;

    @NotBlank(message = "模版名称不能为空")
    private String templateName;

    @NotBlank(message = "模版内容不能为空")
    private String templateContent;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
