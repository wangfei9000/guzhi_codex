package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 报告审核实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "report_review")
public class ReportReview extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 项目ID */
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    /** 报告ID */
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    /** 审核人 */
    @Column(length = 50)
    private String reviewer;

    /** 审核日期 */
    private LocalDate reviewDate;

    /** 审核意见 */
    @Column(columnDefinition = "TEXT")
    private String reviewOpinion;

    /** 审核结果 */
    @Column(length = 50)
    private String reviewResult;
}
