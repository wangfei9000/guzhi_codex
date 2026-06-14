package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 抵押物实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "collateral")
public class Collateral extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 项目ID */
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    /** 抵押物编号 */
    @Column(nullable = false, unique = true, length = 50)
    private String collateralCode;

    /** 抵押物类型 */
    @Column(length = 50)
    private String collateralType;

    /** 抵押物名称 */
    @Column(length = 200)
    private String collateralName;

    /** 抵押物地址 */
    @Column(length = 255)
    private String collateralAddress;

    /** 是否主抵押物 */
    @Column(name = "is_primary")
    private Boolean primaryCollateral;

    /** 实际用途 */
    @Column(length = 100)
    private String actualUse;

    /** 占用状态 */
    @Column(length = 100)
    private String occupancyStatus;

    /** 装修情况 */
    @Column(length = 100)
    private String decoration;

    /** 朝向 */
    @Column(length = 50)
    private String orientation;

    /** 所在层 */
    @Column(length = 50)
    private String currentFloor;

    /** 室内层高 */
    @Column(length = 50)
    private String indoorHeight;

    /** 空间布局 */
    @Column(columnDefinition = "TEXT")
    private String spaceLayout;

    /** 设施设备 */
    @Column(columnDefinition = "TEXT")
    private String facilitiesCondition;

    /** 维护养护情况及完损程度 */
    @Column(columnDefinition = "TEXT")
    private String maintenanceCondition;

    /** 所在宗地形状 */
    @Column(columnDefinition = "TEXT")
    private String parcelShape;

    /** 地形地貌 */
    @Column(columnDefinition = "TEXT")
    private String terrain;

    /** 地势 */
    @Column(columnDefinition = "TEXT")
    private String landLevel;

    /** 土壤情况 */
    @Column(columnDefinition = "TEXT")
    private String soilCondition;

    /** 土地开发程度 */
    @Column(columnDefinition = "TEXT")
    private String landDevelopmentLevel;

    /** 景观 */
    @Column(columnDefinition = "TEXT")
    private String landscape;

    /** 周边环境 */
    @Column(columnDefinition = "TEXT")
    private String surroundingEnvironment;

    /** 建筑面积 */
    @Column(precision = 14, scale = 2)
    private BigDecimal buildingArea;

    /** 土地面积 */
    @Column(precision = 14, scale = 2)
    private BigDecimal landArea;

    /** 小区名称 */
    @Column(length = 100)
    private String communityName;

    /** 楼栋 */
    @Column(length = 50)
    private String building;

    /** 单元 */
    @Column(name = "unit_name", length = 50)
    private String unitName;

    /** 门牌号 */
    @Column(length = 50)
    private String doorNumber;

    /** 建成年份 */
    private Integer buildYear;

    /** 建设用地面积 */
    @Column(precision = 14, scale = 2)
    private BigDecimal constructionLand;

    /** 取得土地面积 */
    @Column(precision = 14, scale = 2)
    private BigDecimal landAcquisition;

    /** 容积率 */
    @Column(precision = 10, scale = 4)
    private BigDecimal floorAreaRatio;

    /** 地上比率 */
    @Column(precision = 10, scale = 4)
    private BigDecimal aboveGroundRatio;

    /** 人防面积 */
    @Column(precision = 14, scale = 2)
    private BigDecimal civilDefenseArea;

    /** 地下比率 */
    @Column(precision = 10, scale = 4)
    private BigDecimal undergroundRatio;

    /** 绿化率 */
    @Column(precision = 10, scale = 4)
    private BigDecimal greeningRate;

    /** 建筑密度 */
    @Column(precision = 10, scale = 4)
    private BigDecimal buildingDensity;

    /** 建筑高度 */
    @Column(precision = 10, scale = 2)
    private BigDecimal buildingHeight;

    /** 楼层数 */
    private Integer floorCount;

    /** 户数 */
    private Integer householdCount;

    /** 车位数 */
    private Integer parkingCount;

    /** 车位配比率 */
    @Column(precision = 10, scale = 4)
    private BigDecimal parkingRatio;

    /** 竣工日期 */
    private LocalDate completionDate;

    /** 产权年限 */
    private Integer propertyRightsYears;

    /** 土地使用年限 */
    private Integer landUseYears;
}
