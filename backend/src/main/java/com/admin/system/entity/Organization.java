package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机构实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_organization")
public class Organization extends BaseEntity {

    /** 机构ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 机构类型 */
    @Column(length = 100)
    private String organizationType;

    /** 机构名称 */
    @Column(nullable = false, length = 200)
    private String organizationName;

    /** 机构联系人 */
    @Column(length = 100)
    private String contactName;

    /** 机构联系人电话 */
    @Column(length = 30)
    private String contactPhone;

    /** 报告模版ID */
    @Column(name = "report_template_id")
    private Long reportTemplateId;
}
