package com.admin.system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 抵押物DTO
 */
@Data
public class CollateralDto {
    /** 主键ID */
    private Long id;
    /** 抵押物编号 */
    private String collateralCode;
    /** 抵押物类型 */
    private String collateralType;
    /** 抵押物名称 */
    private String collateralName;
    /** 抵押物地址 */
    private String collateralAddress;
    /** 是否主抵押物 */
    private Boolean primaryCollateral;
    /** 实际用途 */
    private String actualUse;
    /** 占用状态 */
    private String occupancyStatus;
    /** 装修情况 */
    private String decoration;
    /** 朝向 */
    private String orientation;
    /** 所在层 */
    private String currentFloor;
    /** 室内层高 */
    private String indoorHeight;
    /** 空间布局 */
    private String spaceLayout;
    /** 设施设备 */
    private String facilitiesCondition;
    /** 维护养护情况及完损程度 */
    private String maintenanceCondition;
    /** 所在宗地形状 */
    private String parcelShape;
    /** 地形地貌 */
    private String terrain;
    /** 地势 */
    private String landLevel;
    /** 土壤情况 */
    private String soilCondition;
    /** 土地开发程度 */
    private String landDevelopmentLevel;
    /** 景观 */
    private String landscape;
    /** 周边环境 */
    private String surroundingEnvironment;
    /** 建筑面积 */
    private BigDecimal buildingArea;
    /** 土地面积 */
    private BigDecimal landArea;
    /** 小区名称 */
    private String communityName;
    /** 楼栋 */
    private String building;
    /** 单元 */
    private String unitName;
    /** 门牌号 */
    private String doorNumber;
    /** 建成年份 */
    private Integer buildYear;
    /** 建设用地面积 */
    private BigDecimal constructionLand;
    /** 取得土地面积 */
    private BigDecimal landAcquisition;
    /** 容积率 */
    private BigDecimal floorAreaRatio;
    /** 地上比率 */
    private BigDecimal aboveGroundRatio;
    /** 人防面积 */
    private BigDecimal civilDefenseArea;
    /** 地下比率 */
    private BigDecimal undergroundRatio;
    /** 绿化率 */
    private BigDecimal greeningRate;
    /** 建筑密度 */
    private BigDecimal buildingDensity;
    /** 建筑高度 */
    private BigDecimal buildingHeight;
    /** 楼层数 */
    private Integer floorCount;
    /** 户数 */
    private Integer householdCount;
    /** 车位数 */
    private Integer parkingCount;
    /** 车位配比率 */
    private BigDecimal parkingRatio;
    /** 竣工日期 */
    private LocalDate completionDate;
    /** 产权年限 */
    private Integer propertyRightsYears;
    /** 土地使用年限 */
    private Integer landUseYears;
}
