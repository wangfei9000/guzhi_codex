package com.admin.system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目DTO
 */
@Data
public class ProjectDto {
    /** 主键ID */
    private Long id;
    /** 项目编号 */
    private String projectCode;
    /** 项目名称 */
    private String projectName;
    /** 城市 */
    private String city;
    /** 行政区 */
    private String district;
    /** 片区 */
    private String area;
    /** 地址 */
    private String address;
    /** 登记人 */
    private String registrar;
    /** 登记日期 */
    private LocalDate registrationDate;
    /** 委托单位/委托人 */
    private String clientName;
    /** 委托方联系人 */
    private String clientContact;
    /** 委托方电话 */
    private String clientPhone;
    /** 抵押人姓名/名称 */
    private String mortgagorName;
    /** 抵押人证件号 */
    private String mortgagorIdCard;
    /** 抵押人电话 */
    private String mortgagorPhone;
    /** 借款人姓名/名称 */
    private String borrowerName;
    /** 借款人证件号 */
    private String borrowerIdCard;
    /** 估价目的 */
    private String valuationPurpose;
    /** 估价时点 */
    private LocalDate valuationTime;
    /** 期望价格 */
    private BigDecimal expectedPrice;
    /** 估值单价 */
    private BigDecimal valuationUnitPrice;
    /** 估值总价 */
    private BigDecimal valuationTotalPrice;
    /** 估值类型 */
    private String valuationType;
    /** 建筑面积 */
    private BigDecimal buildingArea;
    /** 状态 */
    private String status;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
