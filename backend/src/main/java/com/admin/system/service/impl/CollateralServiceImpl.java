package com.admin.system.service.impl;

import com.admin.system.dto.CollateralDto;
import com.admin.system.entity.Collateral;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.CollateralRepository;
import com.admin.system.service.CollateralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollateralServiceImpl implements CollateralService {

    private final CollateralRepository collateralRepository;

    @Override
    public List<CollateralDto> listByProjectId(Long projectId) {
        return collateralRepository.findByProjectId(projectId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public CollateralDto getById(Long id) {
        return toDto(collateralRepository.findById(id)
                .orElseThrow(() -> new BusinessException("抵押物不存在")));
    }

    @Override
    public CollateralDto create(Long projectId, CollateralDto dto) {
        Collateral c = toEntity(dto, projectId);
        return toDto(collateralRepository.save(c));
    }

    @Override
    public CollateralDto update(Long id, CollateralDto dto) {
        Collateral c = collateralRepository.findById(id)
                .orElseThrow(() -> new BusinessException("抵押物不存在"));
        applyDto(c, dto);
        return toDto(collateralRepository.save(c));
    }

    @Override
    public void delete(Long id) {
        collateralRepository.deleteById(id);
    }

    private CollateralDto toDto(Collateral c) {
        CollateralDto dto = new CollateralDto();
        dto.setId(c.getId());
        dto.setCollateralCode(c.getCollateralCode());
        dto.setCollateralType(c.getCollateralType());
        dto.setCollateralName(c.getCollateralName());
        dto.setCollateralAddress(c.getCollateralAddress());
        dto.setPrimaryCollateral(c.getPrimaryCollateral());
        dto.setActualUse(c.getActualUse());
        dto.setOccupancyStatus(c.getOccupancyStatus());
        dto.setDecoration(c.getDecoration());
        dto.setOrientation(c.getOrientation());
        dto.setCurrentFloor(c.getCurrentFloor());
        dto.setIndoorHeight(c.getIndoorHeight());
        dto.setSpaceLayout(c.getSpaceLayout());
        dto.setFacilitiesCondition(c.getFacilitiesCondition());
        dto.setMaintenanceCondition(c.getMaintenanceCondition());
        dto.setParcelShape(c.getParcelShape());
        dto.setTerrain(c.getTerrain());
        dto.setLandLevel(c.getLandLevel());
        dto.setSoilCondition(c.getSoilCondition());
        dto.setLandDevelopmentLevel(c.getLandDevelopmentLevel());
        dto.setLandscape(c.getLandscape());
        dto.setSurroundingEnvironment(c.getSurroundingEnvironment());
        dto.setBuildingArea(c.getBuildingArea());
        dto.setLandArea(c.getLandArea());
        dto.setCommunityName(c.getCommunityName());
        dto.setBuilding(c.getBuilding());
        dto.setUnitName(c.getUnitName());
        dto.setDoorNumber(c.getDoorNumber());
        dto.setBuildYear(c.getBuildYear());
        dto.setConstructionLand(c.getConstructionLand());
        dto.setLandAcquisition(c.getLandAcquisition());
        dto.setFloorAreaRatio(c.getFloorAreaRatio());
        dto.setAboveGroundRatio(c.getAboveGroundRatio());
        dto.setCivilDefenseArea(c.getCivilDefenseArea());
        dto.setUndergroundRatio(c.getUndergroundRatio());
        dto.setGreeningRate(c.getGreeningRate());
        dto.setBuildingDensity(c.getBuildingDensity());
        dto.setBuildingHeight(c.getBuildingHeight());
        dto.setFloorCount(c.getFloorCount());
        dto.setHouseholdCount(c.getHouseholdCount());
        dto.setParkingCount(c.getParkingCount());
        dto.setParkingRatio(c.getParkingRatio());
        dto.setCompletionDate(c.getCompletionDate());
        dto.setPropertyRightsYears(c.getPropertyRightsYears());
        dto.setLandUseYears(c.getLandUseYears());
        return dto;
    }

    private Collateral toEntity(CollateralDto dto, Long projectId) {
        Collateral c = new Collateral();
        c.setProjectId(projectId);
        applyDto(c, dto);
        return c;
    }

    private void applyDto(Collateral c, CollateralDto dto) {
        c.setCollateralCode(dto.getCollateralCode());
        c.setCollateralType(dto.getCollateralType());
        c.setCollateralName(dto.getCollateralName());
        c.setCollateralAddress(dto.getCollateralAddress());
        c.setPrimaryCollateral(dto.getPrimaryCollateral());
        c.setActualUse(dto.getActualUse());
        c.setOccupancyStatus(dto.getOccupancyStatus());
        c.setDecoration(dto.getDecoration());
        c.setOrientation(dto.getOrientation());
        c.setCurrentFloor(dto.getCurrentFloor());
        c.setIndoorHeight(dto.getIndoorHeight());
        c.setSpaceLayout(dto.getSpaceLayout());
        c.setFacilitiesCondition(dto.getFacilitiesCondition());
        c.setMaintenanceCondition(dto.getMaintenanceCondition());
        c.setParcelShape(dto.getParcelShape());
        c.setTerrain(dto.getTerrain());
        c.setLandLevel(dto.getLandLevel());
        c.setSoilCondition(dto.getSoilCondition());
        c.setLandDevelopmentLevel(dto.getLandDevelopmentLevel());
        c.setLandscape(dto.getLandscape());
        c.setSurroundingEnvironment(dto.getSurroundingEnvironment());
        c.setBuildingArea(dto.getBuildingArea());
        c.setLandArea(dto.getLandArea());
        c.setCommunityName(dto.getCommunityName());
        c.setBuilding(dto.getBuilding());
        c.setUnitName(dto.getUnitName());
        c.setDoorNumber(dto.getDoorNumber());
        c.setBuildYear(dto.getBuildYear());
        c.setConstructionLand(dto.getConstructionLand());
        c.setLandAcquisition(dto.getLandAcquisition());
        c.setFloorAreaRatio(dto.getFloorAreaRatio());
        c.setAboveGroundRatio(dto.getAboveGroundRatio());
        c.setCivilDefenseArea(dto.getCivilDefenseArea());
        c.setUndergroundRatio(dto.getUndergroundRatio());
        c.setGreeningRate(dto.getGreeningRate());
        c.setBuildingDensity(dto.getBuildingDensity());
        c.setBuildingHeight(dto.getBuildingHeight());
        c.setFloorCount(dto.getFloorCount());
        c.setHouseholdCount(dto.getHouseholdCount());
        c.setParkingCount(dto.getParkingCount());
        c.setParkingRatio(dto.getParkingRatio());
        c.setCompletionDate(dto.getCompletionDate());
        c.setPropertyRightsYears(dto.getPropertyRightsYears());
        c.setLandUseYears(dto.getLandUseYears());
    }
}
