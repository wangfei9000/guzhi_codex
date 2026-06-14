package com.admin.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 外勘列表DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyListDto {
    /** 外勘ID */
    private Long surveyId;
    /** 外勘编号 */
    private String surveyCode;
    /** 4位外勘码 */
    private String code;
    /** 外勘状态 */
    private String surveyStatus;
    /** 项目编号 */
    private String projectCode;
    /** 勘查人 */
    private String surveyor;
    /** 接待人 */
    private String receptionist;
    /** 接待人电话 */
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
    private String ownershipDispute;
    /** 备注 */
    private String remark;
    /** 项目地址 */
    private String projectAddress;
    /** 项目ID */
    private Long projectId;
}
