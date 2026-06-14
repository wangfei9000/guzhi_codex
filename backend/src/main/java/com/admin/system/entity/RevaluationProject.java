package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 复估项目明细
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "revaluation_project")
public class RevaluationProject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 复估ID */
    @Column(nullable = false)
    private Long revaluationId;

    /** 项目编号 */
    @Column(nullable = false, length = 50)
    private String projectCode;

    /** 单价 */
    @Column(precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /** 总价 */
    @Column(precision = 14, scale = 2)
    private BigDecimal totalPrice;

    /** 备注 */
    @Column(columnDefinition = "TEXT")
    private String remark;
}
