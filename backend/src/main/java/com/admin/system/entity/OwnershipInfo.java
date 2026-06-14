package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 权属信息实体 - 每个项目对应一条记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ownership_info")
public class OwnershipInfo extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 项目ID */
    @Column(name = "project_id", nullable = false, unique = true)
    private Long projectId;

    /** 权利人 */
    @Column(length = 200)
    private String rightHolder;

    /** 权利证号 */
    @Column(length = 200)
    private String rightCertificateNumber;

    /** 证载坐落 */
    @Column(length = 500)
    private String registeredAddress;

    /** 借款人姓名 */
    @Column(length = 100)
    private String borrowerName;

    /** 借款人身份证 */
    @Column(length = 50)
    private String borrowerIdCard;

    /** 房屋结构 */
    @Column(length = 100)
    private String buildingStructure;

    /** 用途 */
    @Column(length = 200)
    private String usage;

    /** 实际用途 */
    @Column(length = 200)
    private String actualUse;

    /** 装修情况 */
    @Column(length = 100)
    private String decoration;

    /** 证载建筑面积 */
    @Column(precision = 18, scale = 4)
    private BigDecimal registeredBuildingArea;

    /** 所在层 */
    @Column(length = 50)
    private String currentFloor;

    /** 总楼层 */
    private Integer totalFloors;

    /** 权利性质 */
    @Column(length = 200)
    private String rightNature;

    /** 权利类型 */
    @Column(length = 200)
    private String rightType;

    /** 权利状态 */
    @Column(length = 100)
    private String rightStatus;

    /** 权利登记日期 */
    private LocalDate rightRegistrationDate;

    /** 权利注销日期 */
    private LocalDate rightCancellationDate;

    /** 共有情况 */
    @Column(length = 200)
    private String coOwnership;

    /** 土地使用年限 */
    private Integer landUseYears;

    /** 不动产单元编号 */
    @Column(length = 200)
    private String propertyUnitNumber;

    /** 产权来源 */
    @Column(length = 200)
    private String propertySource;

    /** 共有宗地面积 */
    @Column(precision = 18, scale = 4)
    private BigDecimal sharedLandArea;

    /** 分摊土地面积 */
    @Column(precision = 18, scale = 4)
    private BigDecimal allocatedLandArea;

    /** 建成年代 */
    private Integer buildYear;

    /** 建成年代来源 */
    @Column(length = 200)
    private String buildYearSource;

    /** 办理网签日期 */
    private LocalDate onlineSigningDate;

    /** 合同编号 */
    @Column(length = 200)
    private String contractNumber;

    /** 报告出具日期 */
    private LocalDate reportIssueDate;

    /** 价值时点 */
    private LocalDate valuationTimePoint;

    /** 是否完成老旧小区改造 */
    private Boolean oldCommunityRenovation;

    /** 区域繁华度 */
    @Column(length = 200)
    private String areaProsperity;

    /** 市场繁华度 */
    @Column(length = 200)
    private String marketProsperity;

    /** 房屋所有权证 */
    @Column(length = 200)
    private String houseOwnershipCertificate;

    /** 国有土地使用权证号 */
    @Column(length = 200)
    private String stateLandUseCertificateNumber;

    /** 土地用途 */
    @Column(length = 200)
    private String landUse;

    /** 土地使用权来源 */
    @Column(length = 200)
    private String landUseRightSource;

    /** 土地使用开始日期 */
    private LocalDate landUseStartDate;

    /** 土地使用终止日期 */
    private LocalDate landUseEndDate;

    /** 丘权号 */
    @Column(length = 200)
    private String qiuQuanNumber;

    /** 土地使用权面积 */
    @Column(precision = 18, scale = 4)
    private BigDecimal landUseArea;

    /** 抵押信息 */
    @Column(columnDefinition = "TEXT")
    private String mortgageInfo;

    /** 查封信息 */
    @Column(columnDefinition = "TEXT")
    private String seizureInfo;

    /** 租赁限制 */
    @Column(columnDefinition = "TEXT")
    private String leaseRestriction;

    /** 其他权属信息 */
    @Column(columnDefinition = "TEXT")
    private String otherRightsInfo;

    /** 备注 */
    @Column(columnDefinition = "TEXT")
    private String remark;
}
