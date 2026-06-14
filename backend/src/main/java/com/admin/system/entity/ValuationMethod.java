package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 估价方法实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "valuation_method")
public class ValuationMethod extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 方法编号 */
    @Column(nullable = false, unique = true, length = 50)
    private String methodCode;

    /** 方法名称 */
    @Column(nullable = false, length = 100)
    private String methodName;

    /** 权重 */
    @Column(precision = 5, scale = 4)
    private BigDecimal weight;

    /** 单价 */
    @Column(precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /** 估价师签名 */
    @Column(length = 50)
    private String appraiserSignature;

    /** 方法描述 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 报告ID */
    @Column(name = "report_id", nullable = false)
    private Long reportId;
}
