package com.admin.system.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 报告盖章DTO
 */
@Data
public class ReportSealDto {
    /** 主键ID */
    private Long id;
    /** 报告ID */
    private Long reportId;
    /** 项目ID */
    private Long projectId;
    /** 盖章报告URL */
    private String sealedReportUrl;
    /** 盖章人 */
    private String sealer;
    /** 盖章日期 */
    private LocalDate sealDate;
}
