package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 排班/调度实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_schedule")
public class SysSchedule extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登记日期 */
    @Column(nullable = false)
    private LocalDate registrationDate;

    /** 编号 */
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    /** 报告编号 */
    @Column(nullable = false, unique = true, length = 30)
    private String reportNo;

    /** 接单人 */
    @Column(length = 50)
    private String orderTaker;

    /** 机构 */
    @Column(length = 100)
    private String agency;

    /** 报告人 */
    @Column(length = 50)
    private String reporter;

    /** 报告人电话 */
    @Column(length = 20)
    private String reporterPhone;

    /** 联系人 */
    @Column(length = 50)
    private String contact;

    /** 联系电话 */
    @Column(length = 20)
    private String contactPhone;

    /** 客服 */
    @Column(length = 50)
    private String customerService;

    /** 项目地址 */
    @Column(nullable = false, length = 255)
    private String projectAddress;

    /** 勘查人 */
    @Column(length = 50)
    private String surveyor;

    /** 估价师 */
    @Column(length = 50)
    private String appraiser;

    /** 状态 */
    @Column(nullable = false, length = 20)
    private String status;

    /** 单价 */
    @Column(precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /** 总价 */
    @Column(precision = 14, scale = 2)
    private BigDecimal totalPrice;
}
