package com.admin.system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排班调度DTO
 */
@Data
public class ScheduleDto {
    /** 主键ID */
    private Long id;
    /** 登记日期 */
    private LocalDate registrationDate;
    /** 编号 */
    private String code;
    /** 报告编号 */
    private String reportNo;
    /** 接单人 */
    private String orderTaker;
    /** 机构 */
    private String agency;
    /** 报告人 */
    private String reporter;
    /** 报告人电话 */
    private String reporterPhone;
    /** 联系人 */
    private String contact;
    /** 联系电话 */
    private String contactPhone;
    /** 客服 */
    private String customerService;
    /** 项目地址 */
    private String projectAddress;
    /** 勘查人 */
    private String surveyor;
    /** 估价师 */
    private String appraiser;
    /** 状态 */
    private String status;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 总价 */
    private BigDecimal totalPrice;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
