package com.admin.system.service.impl;

import com.admin.system.dto.OwnershipInfoDto;
import com.admin.system.entity.OwnershipInfo;
import com.admin.system.repository.OwnershipInfoRepository;
import com.admin.system.service.OwnershipInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OwnershipInfoServiceImpl implements OwnershipInfoService {

    private final OwnershipInfoRepository ownershipInfoRepository;

    @Override
    public OwnershipInfoDto getByProjectId(Long projectId) {
        Optional<OwnershipInfo> opt = ownershipInfoRepository.findByProjectId(projectId);
        return opt.map(this::toDto).orElse(null);
    }

    @Override
    @Transactional
    public OwnershipInfoDto save(Long projectId, OwnershipInfoDto dto) {
        OwnershipInfo o = ownershipInfoRepository.findByProjectId(projectId)
                .orElseGet(() -> {
                    OwnershipInfo newEntity = new OwnershipInfo();
                    newEntity.setProjectId(projectId);
                    return newEntity;
                });
        applyDto(o, dto);
        o = ownershipInfoRepository.save(o);
        return toDto(o);
    }

    private OwnershipInfoDto toDto(OwnershipInfo o) {
        OwnershipInfoDto dto = new OwnershipInfoDto();
        dto.setId(o.getId());
        dto.setRightHolder(o.getRightHolder());
        dto.setRightCertificateNumber(o.getRightCertificateNumber());
        dto.setRegisteredAddress(o.getRegisteredAddress());
        dto.setBorrowerName(o.getBorrowerName());
        dto.setBorrowerIdCard(o.getBorrowerIdCard());
        dto.setBuildingStructure(o.getBuildingStructure());
        dto.setUsage(o.getUsage());
        dto.setActualUse(o.getActualUse());
        dto.setDecoration(o.getDecoration());
        dto.setRegisteredBuildingArea(o.getRegisteredBuildingArea());
        dto.setCurrentFloor(o.getCurrentFloor());
        dto.setTotalFloors(o.getTotalFloors());
        dto.setRightNature(o.getRightNature());
        dto.setRightType(o.getRightType());
        dto.setRightStatus(o.getRightStatus());
        dto.setRightRegistrationDate(o.getRightRegistrationDate());
        dto.setRightCancellationDate(o.getRightCancellationDate());
        dto.setCoOwnership(o.getCoOwnership());
        dto.setLandUseYears(o.getLandUseYears());
        dto.setPropertyUnitNumber(o.getPropertyUnitNumber());
        dto.setPropertySource(o.getPropertySource());
        dto.setSharedLandArea(o.getSharedLandArea());
        dto.setAllocatedLandArea(o.getAllocatedLandArea());
        dto.setBuildYear(o.getBuildYear());
        dto.setBuildYearSource(o.getBuildYearSource());
        dto.setOnlineSigningDate(o.getOnlineSigningDate());
        dto.setContractNumber(o.getContractNumber());
        dto.setReportIssueDate(o.getReportIssueDate());
        dto.setValuationTimePoint(o.getValuationTimePoint());
        dto.setOldCommunityRenovation(o.getOldCommunityRenovation());
        dto.setAreaProsperity(o.getAreaProsperity());
        dto.setMarketProsperity(o.getMarketProsperity());
        dto.setHouseOwnershipCertificate(o.getHouseOwnershipCertificate());
        dto.setStateLandUseCertificateNumber(o.getStateLandUseCertificateNumber());
        dto.setLandUse(o.getLandUse());
        dto.setLandUseRightSource(o.getLandUseRightSource());
        dto.setLandUseStartDate(o.getLandUseStartDate());
        dto.setLandUseEndDate(o.getLandUseEndDate());
        dto.setQiuQuanNumber(o.getQiuQuanNumber());
        dto.setLandUseArea(o.getLandUseArea());
        dto.setMortgageInfo(o.getMortgageInfo());
        dto.setSeizureInfo(o.getSeizureInfo());
        dto.setLeaseRestriction(o.getLeaseRestriction());
        dto.setOtherRightsInfo(o.getOtherRightsInfo());
        dto.setRemark(o.getRemark());
        return dto;
    }

    private void applyDto(OwnershipInfo o, OwnershipInfoDto dto) {
        o.setRightHolder(dto.getRightHolder());
        o.setRightCertificateNumber(dto.getRightCertificateNumber());
        o.setRegisteredAddress(dto.getRegisteredAddress());
        o.setBorrowerName(dto.getBorrowerName());
        o.setBorrowerIdCard(dto.getBorrowerIdCard());
        o.setBuildingStructure(dto.getBuildingStructure());
        o.setUsage(dto.getUsage());
        o.setActualUse(dto.getActualUse());
        o.setDecoration(dto.getDecoration());
        o.setRegisteredBuildingArea(dto.getRegisteredBuildingArea());
        o.setCurrentFloor(dto.getCurrentFloor());
        o.setTotalFloors(dto.getTotalFloors());
        o.setRightNature(dto.getRightNature());
        o.setRightType(dto.getRightType());
        o.setRightStatus(dto.getRightStatus());
        o.setRightRegistrationDate(dto.getRightRegistrationDate());
        o.setRightCancellationDate(dto.getRightCancellationDate());
        o.setCoOwnership(dto.getCoOwnership());
        o.setLandUseYears(dto.getLandUseYears());
        o.setPropertyUnitNumber(dto.getPropertyUnitNumber());
        o.setPropertySource(dto.getPropertySource());
        o.setSharedLandArea(dto.getSharedLandArea());
        o.setAllocatedLandArea(dto.getAllocatedLandArea());
        o.setBuildYear(dto.getBuildYear());
        o.setBuildYearSource(dto.getBuildYearSource());
        o.setOnlineSigningDate(dto.getOnlineSigningDate());
        o.setContractNumber(dto.getContractNumber());
        o.setReportIssueDate(dto.getReportIssueDate());
        o.setValuationTimePoint(dto.getValuationTimePoint());
        o.setOldCommunityRenovation(dto.getOldCommunityRenovation());
        o.setAreaProsperity(dto.getAreaProsperity());
        o.setMarketProsperity(dto.getMarketProsperity());
        o.setHouseOwnershipCertificate(dto.getHouseOwnershipCertificate());
        o.setStateLandUseCertificateNumber(dto.getStateLandUseCertificateNumber());
        o.setLandUse(dto.getLandUse());
        o.setLandUseRightSource(dto.getLandUseRightSource());
        o.setLandUseStartDate(dto.getLandUseStartDate());
        o.setLandUseEndDate(dto.getLandUseEndDate());
        o.setQiuQuanNumber(dto.getQiuQuanNumber());
        o.setLandUseArea(dto.getLandUseArea());
        o.setMortgageInfo(dto.getMortgageInfo());
        o.setSeizureInfo(dto.getSeizureInfo());
        o.setLeaseRestriction(dto.getLeaseRestriction());
        o.setOtherRightsInfo(dto.getOtherRightsInfo());
        o.setRemark(dto.getRemark());
    }
}
