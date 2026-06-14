package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 对账记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "reconciliation_record")
public class ReconciliationRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 机构ID */
    private Long organizationId;

    /** 开始时间 */
    private LocalDate startTime;

    /** 结束时间 */
    private LocalDate endTime;

    /** 对账日期 */
    private LocalDate reconciliationDate;

    /** 对账结果：进行中 / 已完成 */
    @Column(nullable = false, length = 20)
    private String result;

    /** 文件URL */
    @Column(length = 500)
    private String fileUrl;

    /** 备注 */
    @Column(columnDefinition = "TEXT")
    private String remark;
}
