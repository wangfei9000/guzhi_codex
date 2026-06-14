package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 复估记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "revaluation_record")
public class RevaluationRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 机构ID */
    private Long organizationId;

    /** 复估日期 */
    private LocalDate revaluationDate;

    /** 复估结果：进行中 / 已完成 */
    @Column(nullable = false, length = 20)
    private String result;

    /** 文件URL */
    @Column(length = 500)
    private String fileUrl;

    /** 备注 */
    @Column(columnDefinition = "TEXT")
    private String remark;
}
