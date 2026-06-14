package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 机构DTO
 */
@Data
public class OrganizationDto {
    /** 机构ID */
    private Long id;
    /** 机构类型 */
    private String organizationType;
    /** 机构名称 */
    @NotBlank(message = "机构名称不能为空")
    private String organizationName;
    /** 机构联系人 */
    private String contactName;
    /** 机构联系人电话 */
    private String contactPhone;
    /** 报告模版ID */
    private Long reportTemplateId;
    /** 报告模版名称 */
    private String reportTemplateName;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
