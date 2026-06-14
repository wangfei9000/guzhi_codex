package com.admin.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 盖章列表DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SealListDto {
    /** 盖章ID */
    private Long sealId;
    /** 报告ID */
    private Long reportId;
    /** 报告编号 */
    private String reportCode;
    /** 项目编号 */
    private String projectCode;
    /** 盖章报告URL */
    private String sealedReportUrl;
    /** 盖章人 */
    private String sealer;
    /** 盖章日期 */
    private LocalDate sealDate;
    /** 项目状态 */
    private String projectStatus;
}
