package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 项目编号 */
    @Column(nullable = false, unique = true, length = 50)
    private String projectCode;

    /** 项目名称 */
    @Column(nullable = false, length = 200)
    private String projectName;

    /** 城市 */
    @Column(length = 50)
    private String city;

    /** 行政区 */
    @Column(length = 50)
    private String district;

    /** 片区 */
    @Column(length = 100)
    private String area;

    /** 地址 */
    @Column(length = 255)
    private String address;

    /** 登记人 */
    @Column(length = 50)
    private String registrar;

    /** 登记日期 */
    private LocalDate registrationDate;

    /** 委托单位/委托人 */
    @Column(length = 200)
    private String clientName;

    /** 委托方联系人 */
    @Column(length = 50)
    private String clientContact;

    /** 委托方电话 */
    @Column(length = 20)
    private String clientPhone;

    /** 抵押人姓名/名称 */
    @Column(length = 200)
    private String mortgagorName;

    /** 抵押人证件号 */
    @Column(length = 50)
    private String mortgagorIdCard;

    /** 抵押人电话 */
    @Column(length = 20)
    private String mortgagorPhone;

    /** 借款人姓名/名称 */
    @Column(length = 200)
    private String borrowerName;

    /** 借款人证件号 */
    @Column(length = 50)
    private String borrowerIdCard;

    /** 估价目的 */
    @Column(length = 100)
    private String valuationPurpose;

    /** 估价时点 */
    private LocalDate valuationTime;

    /** 期望价格 */
    @Column(precision = 14, scale = 2)
    private BigDecimal expectedPrice;

    /** 估值单价 */
    @Column(precision = 12, scale = 2)
    private BigDecimal valuationUnitPrice;

    /** 估值总价 */
    @Column(precision = 14, scale = 2)
    private BigDecimal valuationTotalPrice;

    /** 估值类型：人工估值 / 自动估值 */
    @Column(length = 20)
    private String valuationType;

    /** 建筑面积 */
    @Column(precision = 14, scale = 2)
    private BigDecimal buildingArea;

    /** 状态 */
    @Column(nullable = false, length = 20)
    private String status;

    /** 备注 */
    @Column(columnDefinition = "TEXT")
    private String remark;
}
