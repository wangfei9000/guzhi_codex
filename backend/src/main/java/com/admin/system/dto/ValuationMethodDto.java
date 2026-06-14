package com.admin.system.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 估价方法DTO
 */
@Data
public class ValuationMethodDto {
    /** 主键ID */
    private Long id;
    /** 方法编号 */
    private String methodCode;
    /** 方法名称 */
    private String methodName;
    /** 权重 */
    private BigDecimal weight;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 估价师签名 */
    private String appraiserSignature;
    /** 方法描述 */
    private String description;
    /** 报告ID */
    private Long reportId;
}
