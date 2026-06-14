package com.admin.system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 估价报告DTO
 */
@Data
public class ValuationReportDto {
    /** 估价方法列表 */
    private List<ValuationMethodDto> valuationMethods;
    /** 报告审核列表 */
    private List<ReportReviewDto> reportReviews;
    /** 主键ID */
    private Long id;
    /** 报告编号 */
    private String reportCode;
    /** 开始时间 */
    private LocalDateTime startTime;
    /** 结束时间 */
    private LocalDateTime endTime;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 总价（万元） */
    private BigDecimal totalPrice;
    /** 抵押价值（万元） */
    private BigDecimal mortgageValue;
    /** 法定优先受偿款（元） */
    private BigDecimal priorityCompensationAmount;
    /** 法定优先受偿款说明 */
    private String priorityCompensationDescription;
    /** 价值时点 */
    private LocalDate valueDate;
    /** 报告出具日期 */
    private LocalDate reportIssueDate;
    /** 报告有效期开始日期 */
    private LocalDate validStartDate;
    /** 报告有效期结束日期 */
    private LocalDate validEndDate;
    /** 估价师1姓名 */
    private String valuer1Name;
    /** 估价师1证号 */
    private String valuer1CertNo;
    /** 估价师2姓名 */
    private String valuer2Name;
    /** 估价师2证号 */
    private String valuer2CertNo;
    /** 估价结果 */
    private String valuationResult;
    /** 区域评估 */
    private String areaEvaluation;
    /** 周边成交 */
    private String surroundingTransactions;
    /** 变现能力分析 */
    private String liquidityAnalysis;
    /** 户型分析 */
    private String floorPlan;
    /** 土地出让金扣除 */
    private BigDecimal landGrantDeduction;
    /** 装修成新率 */
    private BigDecimal decorationNewRate;
    /** 设备成新率 */
    private BigDecimal equipmentNewRate;
    /** 报告文件URL */
    private String reportUrl;
    /** 银行建议 */
    private String bankSuggestion;
    /** 土地宗地图 */
    private String landPlot;
}
