package com.admin.system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 权属信息DTO
 */
@Data
public class OwnershipInfoDto {
    /** 主键ID */
    private Long id;

    /** 权利人 */
    private String rightHolder;

    /** 权利证号 */
    private String rightCertificateNumber;

    /** 证载坐落 */
    private String registeredAddress;

    /** 借款人姓名 */
    private String borrowerName;

    /** 借款人身份证 */
    private String borrowerIdCard;

    /** 房屋结构 */
    private String buildingStructure;

    /** 用途 */
    private String usage;

    /** 实际用途 */
    private String actualUse;

    /** 装修情况 */
    private String decoration;

    /** 证载建筑面积 */
    private BigDecimal registeredBuildingArea;

    /** 所在层 */
    private String currentFloor;

    /** 总楼层 */
    private Integer totalFloors;

    /** 权利性质 */
    private String rightNature;

    /** 权利类型 */
    private String rightType;

    /** 权利状态 */
    private String rightStatus;

    /** 权利登记日期 */
    private LocalDate rightRegistrationDate;

    /** 权利注销日期 */
    private LocalDate rightCancellationDate;

    /** 共有情况 */
    private String coOwnership;

    /** 土地使用年限 */
    private Integer landUseYears;

    /** 不动产单元编号 */
    private String propertyUnitNumber;

    /** 产权来源 */
    private String propertySource;

    /** 共有宗地面积 */
    private BigDecimal sharedLandArea;

    /** 分摊土地面积 */
    private BigDecimal allocatedLandArea;

    /** 建成年代 */
    private Integer buildYear;

    /** 建成年代来源 */
    private String buildYearSource;

    /** 办理网签日期 */
    private LocalDate onlineSigningDate;

    /** 合同编号 */
    private String contractNumber;

    /** 报告出具日期 */
    private LocalDate reportIssueDate;

    /** 价值时点 */
    private LocalDate valuationTimePoint;

    /** 是否完成老旧小区改造 */
    private Boolean oldCommunityRenovation;

    /** 区域繁华度 */
    private String areaProsperity;

    /** 市场繁华度 */
    private String marketProsperity;

    /** 房屋所有权证 */
    private String houseOwnershipCertificate;

    /** 国有土地使用权证号 */
    private String stateLandUseCertificateNumber;

    /** 土地用途 */
    private String landUse;

    /** 土地使用权来源 */
    private String landUseRightSource;

    /** 土地使用开始日期 */
    private LocalDate landUseStartDate;

    /** 土地使用终止日期 */
    private LocalDate landUseEndDate;

    /** 丘权号 */
    private String qiuQuanNumber;

    /** 土地使用权面积 */
    private BigDecimal landUseArea;

    /** 抵押信息 */
    private String mortgageInfo;

    /** 查封信息 */
    private String seizureInfo;

    /** 租赁限制 */
    private String leaseRestriction;

    /** 其他权属信息 */
    private String otherRightsInfo;

    /** 备注 */
    private String remark;
}
