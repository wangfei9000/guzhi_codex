package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 外勘照片实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "survey_photo")
public class SurveyPhoto extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 项目ID */
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    /** 外勘ID */
    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    /** 照片编号 */
    @Column(nullable = false, unique = true, length = 50)
    private String photoCode;

    /** 照片路径 */
    @Column(length = 500)
    private String photoPath;

    /** 照片分类 */
    @Column(length = 50)
    private String photoCategory;

    /** 照片描述 */
    @Column(columnDefinition = "TEXT")
    private String photoDescription;
}
