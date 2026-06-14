package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 估值价格实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "valuation_price")
public class ValuationPrice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 城市 */
    @Column(length = 50)
    private String city;

    /** 行政区 */
    @Column(length = 50)
    private String district;

    /** 地址 */
    @Column(length = 500)
    private String address;

    /** 单价 */
    @Column(precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /** 总价 */
    @Column(precision = 14, scale = 2)
    private BigDecimal totalPrice;

    /** 面积 */
    @Column(precision = 14, scale = 2)
    private BigDecimal area;

    /** 估价时点 */
    private LocalDate valuationTime;
}
