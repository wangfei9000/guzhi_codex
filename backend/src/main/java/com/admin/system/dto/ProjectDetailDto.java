package com.admin.system.dto;

import lombok.Data;
import java.util.List;

/**
 * 项目详情DTO（包含所有子实体）
 */
@Data
public class ProjectDetailDto {
    /** 项目信息 */
    private ProjectDto project;
    /** 抵押物列表 */
    private List<CollateralDto> collaterals;
    /** 估价报告列表 */
    private List<ValuationReportDto> valuationReports;
    /** 外勘记录列表 */
    private List<SurveyDto> surveys;
    /** 权属信息（单个） */
    private OwnershipInfoDto ownershipInfo;
}
