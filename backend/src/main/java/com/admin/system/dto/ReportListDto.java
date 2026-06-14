package com.admin.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报告列表DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportListDto {
    /** 报告ID */
    private Long reportId;
    /** 报告编号 */
    private String reportCode;
    /** 项目编号 */
    private String projectCode;
    /** 开始时间 */
    private LocalDateTime startTime;
    /** 结束时间 */
    private LocalDateTime endTime;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 抵押物地址 */
    private String collateralAddress;
    /** 建筑面积 */
    private BigDecimal buildingArea;
    /** 估价结果 */
    private String valuationResult;
    /** 项目状态 */
    private String projectStatus;
    /** 报告URL */
    private String reportUrl;
}
