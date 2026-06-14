package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 报告盖章实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "report_seal")
public class ReportSeal extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 报告ID */
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    /** 项目ID */
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    /** 盖章报告URL */
    @Column(name = "sealed_report_url", length = 500)
    private String sealedReportUrl;

    /** 盖章人 */
    @Column(length = 50)
    private String sealer;

    /** 盖章日期 */
    private LocalDate sealDate;
}
