package com.admin.system.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 外勘记录DTO
 */
@Data
public class SurveyDto {
    /** 照片列表 */
    private List<SurveyPhotoDto> photos;
    /** 主键ID */
    private Long id;
    /** 外勘编号 */
    private String surveyCode;
    /** 4位外勘码 */
    private String code;
    /** 外勘状态 */
    private String surveyStatus;
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
    /** 项目ID */
    private Long projectId;
}
