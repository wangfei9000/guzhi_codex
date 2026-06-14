package com.admin.system.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 报告审核DTO
 */
@Data
public class ReportReviewDto {
    /** 主键ID */
    private Long id;
    /** 报告ID */
    private Long reportId;
    /** 审核人 */
    private String reviewer;
    /** 审核日期 */
    private LocalDate reviewDate;
    /** 审核意见 */
    private String reviewOpinion;
    /** 审核结果 */
    private String reviewResult;
}
