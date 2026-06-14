package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 估价报告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "valuation_report")
public class ValuationReport extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 项目ID */
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    /** 报告编号 */
    @Column(nullable = false, unique = true, length = 50)
    private String reportCode;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 单价 */
    @Column(precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /** 总价（万元） */
    @Column(name = "total_price", precision = 14, scale = 2)
    private BigDecimal totalPrice;

    /** 抵押价值（万元） */
    @Column(name = "mortgage_value", precision = 14, scale = 2)
    private BigDecimal mortgageValue;

    /** 法定优先受偿款（元） */
    @Column(name = "priority_compensation_amount", precision = 14, scale = 2)
    private BigDecimal priorityCompensationAmount;

    /** 法定优先受偿款说明 */
    @Column(name = "priority_compensation_description", columnDefinition = "TEXT")
    private String priorityCompensationDescription;

    /** 价值时点 */
    @Column(name = "value_date")
    private LocalDate valueDate;

    /** 报告出具日期 */
    @Column(name = "report_issue_date")
    private LocalDate reportIssueDate;

    /** 报告有效期开始日期 */
    @Column(name = "valid_start_date")
    private LocalDate validStartDate;

    /** 报告有效期结束日期 */
    @Column(name = "valid_end_date")
    private LocalDate validEndDate;

    /** 估价师1姓名 */
    @Column(name = "valuer1_name", length = 50)
    private String valuer1Name;

    /** 估价师1证号 */
    @Column(name = "valuer1_cert_no", length = 50)
    private String valuer1CertNo;

    /** 估价师2姓名 */
    @Column(name = "valuer2_name", length = 50)
    private String valuer2Name;

    /** 估价师2证号 */
    @Column(name = "valuer2_cert_no", length = 50)
    private String valuer2CertNo;

    /** 估价结果 */
    @Column(columnDefinition = "TEXT")
    private String valuationResult;

    /** 区域评估 */
    @Column(columnDefinition = "TEXT")
    private String areaEvaluation;

    /** 周边成交 */
    @Column(columnDefinition = "TEXT")
    private String surroundingTransactions;

    /** 变现能力分析 */
    @Column(columnDefinition = "TEXT")
    private String liquidityAnalysis;

    /** 户型分析 */
    @Column(columnDefinition = "TEXT")
    private String floorPlan;

    /** 土地出让金扣除 */
    @Column(precision = 14, scale = 2)
    private BigDecimal landGrantDeduction;

    /** 装修成新率 */
    @Column(precision = 5, scale = 4)
    private BigDecimal decorationNewRate;

    /** 设备成新率 */
    @Column(precision = 5, scale = 4)
    private BigDecimal equipmentNewRate;

    /** 报告文件URL */
    @Column(length = 500)
    private String reportUrl;

    /** 银行建议 */
    @Column(columnDefinition = "TEXT")
    private String bankSuggestion;

    /** 土地宗地图 */
    @Column(columnDefinition = "TEXT")
    private String landPlot;
}
