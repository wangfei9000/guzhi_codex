package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 外勘记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "survey")
public class Survey extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 项目ID */
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    /** 外勘编号 */
    @Column(nullable = false, unique = true, length = 50)
    private String surveyCode;

    /** 4位外勘码 */
    @Column(unique = true, length = 4)
    private String code;

    /** 外勘状态 */
    @Column(name = "survey_status", nullable = false, length = 20)
    private String surveyStatus = "未查勘";

    /** 勘查人 */
    @Column(length = 50)
    private String surveyor;

    /** 接待人 */
    @Column(length = 50)
    private String receptionist;

    /** 接待人电话 */
    @Column(length = 20)
    private String receptionistPhone;

    /** 勘查日期 */
    private LocalDate surveyDate;

    /** 开始时间 */
    private LocalTime startTime;

    /** 结束时间 */
    private LocalTime endTime;

    /** 是否验看房产证 */
    private Boolean propertyCertVerified;

    /** 权属争议 */
    @Column(columnDefinition = "TEXT")
    private String ownershipDispute;

    /** 备注 */
    @Column(columnDefinition = "TEXT")
    private String remark;
}
