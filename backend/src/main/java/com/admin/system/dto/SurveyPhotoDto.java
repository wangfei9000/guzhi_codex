package com.admin.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 外勘照片DTO
 */
@Data
public class SurveyPhotoDto {
    /** 主键ID */
    private Long id;
    /** 外勘ID */
    private Long surveyId;
    /** 照片编号 */
    private String photoCode;
    /** 照片路径 */
    private String photoPath;
    /** 照片分类 */
    private String photoCategory;
    /** 照片描述 */
    private String photoDescription;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
