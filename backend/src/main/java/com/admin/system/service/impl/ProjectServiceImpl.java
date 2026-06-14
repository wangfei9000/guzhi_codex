package com.admin.system.service.impl;

import com.admin.system.dto.*;
import com.admin.system.entity.*;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.*;
import com.admin.system.service.ProjectService;
import com.admin.system.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final CollateralRepository collateralRepository;
    private final ValuationReportRepository valuationReportRepository;
    private final ValuationMethodRepository valuationMethodRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyPhotoRepository surveyPhotoRepository;
    private final OwnershipInfoRepository ownershipInfoRepository;
    private final ReportReviewRepository reportReviewRepository;
    private final SurveyService surveyService;
    private final ValuationPriceRepository valuationPriceRepository;


    /**
     * 以后对接外部估值接口用的
     * @param city
     * @param address
     * @param valuationTime
     * @return
     */
    @Override
    public Optional<ValuationPrice> findValuationPrice(String city, String address, LocalDate valuationTime) {
        System.out.println("=================================================");
        System.out.println("调用了估值[city:"+city+", address:"+address+", valuationTime:"+valuationTime+"]");

        if (!StringUtils.hasText(address)) {
            return Optional.empty();
        }
        // valuationTime is reserved for future price-time matching.
        /*if (StringUtils.hasText(city)) {
            String cityVal = city.trim();
            return valuationPriceRepository.findFirstByCityAndAddress(cityVal, address)
                    .or(() -> valuationPriceRepository.findFirstByCityAndAddressContaining(cityVal, address));
        }*/
        return valuationPriceRepository.findFirstByAddress(address)
                .or(() -> valuationPriceRepository.findFirstByAddressContaining(address));
    }




    @Override
    public Page<ProjectDto> listProjects(String projectCode, String projectName, String clientName, String city,
                                         String address, String status, Boolean onlyValuation, Pageable pageable) {
        String codeKw = StringUtils.hasText(projectCode) ? "%" + projectCode + "%" : null;
        String nameKw = StringUtils.hasText(projectName) ? "%" + projectName + "%" : null;
        String clientVal = StringUtils.hasText(clientName) ? clientName.trim() : null;
        String cityKw = StringUtils.hasText(city) ? "%" + city + "%" : null;
        String addrKw = StringUtils.hasText(address) ? "%" + address + "%" : null;
        String statusVal = StringUtils.hasText(status) ? status : null;

        return projectRepository.search(codeKw, nameKw, clientVal, cityKw, addrKw, statusVal,
                        Boolean.TRUE.equals(onlyValuation), pageable)
                .map(this::toDto);
    }

    @Override
    public Page<ProjectDto> listBankValuations(String clientName, String city, String district,
                                               String valuationType, String status, LocalDate valuationTime,
                                               Pageable pageable) {
        if (!StringUtils.hasText(clientName)) {
            return Page.empty(pageable);
        }

        Specification<Project> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("clientName"), clientName.trim()),
                cb.isNotNull(root.get("valuationType"))
        );
        if (StringUtils.hasText(city)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("city"), city));
        }
        if (StringUtils.hasText(district)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("district"), district));
        }
        if (StringUtils.hasText(valuationType)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("valuationType"), valuationType));
        }
        if (StringUtils.hasText(status)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (valuationTime != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("valuationTime"), valuationTime));
        }

        return projectRepository.findAll(spec, pageable)
                .map(this::toDto);
    }

    @Override
    public List<String> listClientNames() {
        return projectRepository.findDistinctClientNames();
    }

    @Override
    public List<String> listValuationCities() {
        return valuationPriceRepository.findDistinctCities();
    }

    @Override
    public List<String> listValuationDistricts(String city) {
        if (!StringUtils.hasText(city)) {
            return List.of();
        }
        return valuationPriceRepository.findDistinctDistrictsByCity(city.trim());
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("项目不存在"));
        return toDto(project);
    }

    @Override
    public ProjectDto updateProject(Long id, ProjectDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("项目不存在"));
        project.setProjectName(dto.getProjectName());
        project.setCity(dto.getCity());
        project.setDistrict(dto.getDistrict());
        project.setArea(dto.getArea());
        project.setAddress(dto.getAddress());
        project.setRegistrar(dto.getRegistrar());
        project.setRegistrationDate(dto.getRegistrationDate());
        project.setClientName(dto.getClientName());
        project.setClientContact(dto.getClientContact());
        project.setClientPhone(dto.getClientPhone());
        project.setMortgagorName(dto.getMortgagorName());
        project.setMortgagorIdCard(dto.getMortgagorIdCard());
        project.setMortgagorPhone(dto.getMortgagorPhone());
        project.setBorrowerName(dto.getBorrowerName());
        project.setBorrowerIdCard(dto.getBorrowerIdCard());
        project.setValuationPurpose(dto.getValuationPurpose());
        project.setValuationTime(dto.getValuationTime());
        project.setExpectedPrice(dto.getExpectedPrice());
        project.setValuationUnitPrice(dto.getValuationUnitPrice());
        project.setValuationTotalPrice(dto.getValuationTotalPrice());
        project.setValuationType(dto.getValuationType());
        project.setBuildingArea(dto.getBuildingArea());
        project.setStatus(dto.getStatus());
        project.setRemark(dto.getRemark());
        return toDto(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto dto, String surveyor) {
        Project project = new Project();
        project.setProjectCode(generateProjectCode());
        project.setProjectName(dto.getProjectName());
        project.setCity(dto.getCity());
        project.setDistrict(dto.getDistrict());
        project.setArea(dto.getArea());
        project.setAddress(dto.getAddress());
        project.setRegistrar(dto.getRegistrar());
        project.setRegistrationDate(dto.getRegistrationDate());
        project.setClientName(dto.getClientName());
        project.setClientContact(dto.getClientContact());
        project.setClientPhone(dto.getClientPhone());
        project.setMortgagorName(dto.getMortgagorName());
        project.setMortgagorIdCard(dto.getMortgagorIdCard());
        project.setMortgagorPhone(dto.getMortgagorPhone());
        project.setBorrowerName(dto.getBorrowerName());
        project.setBorrowerIdCard(dto.getBorrowerIdCard());
        project.setValuationPurpose(dto.getValuationPurpose());
        project.setValuationTime(dto.getValuationTime());
        project.setExpectedPrice(dto.getExpectedPrice());
        project.setValuationUnitPrice(dto.getValuationUnitPrice());
        project.setValuationTotalPrice(dto.getValuationTotalPrice());
        project.setValuationType(dto.getValuationType());
        project.setBuildingArea(dto.getBuildingArea());
        project.setStatus(dto.getStatus() != null ? dto.getStatus() : "未评估");
        project.setRemark(dto.getRemark());
        project = projectRepository.save(project);

        // Auto-create survey record if surveyor is provided
        if (surveyor != null && !surveyor.isBlank()) {
            SurveyDto surveyDto = new SurveyDto();
            surveyDto.setSurveyor(surveyor);
            surveyDto.setReceptionist(dto.getClientContact());
            surveyDto.setReceptionistPhone(dto.getClientPhone());
            surveyService.create(project.getId(), surveyDto);
        }

        return toDto(project);
    }

    private String generateProjectCode() {
        return "PRJ-" + System.currentTimeMillis();
    }

    @Override
    @Transactional
    public ProjectDto saveAutoValuation(ProjectDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getAddress())) {
            throw new BusinessException("地址为空，无法自动估值");
        }

        Optional<ValuationPrice> price = findValuationPrice(dto.getCity(), dto.getAddress(), dto.getValuationTime());

        Project project = dto.getId() != null
                ? projectRepository.findById(dto.getId()).orElseGet(Project::new)
                : new Project();
        if (project.getId() == null) {
            project.setProjectCode(generateProjectCode());
        }

        project.setProjectName(StringUtils.hasText(dto.getProjectName()) ? dto.getProjectName() : dto.getAddress());
        project.setCity(dto.getCity());
        project.setDistrict(dto.getDistrict());
        project.setArea(dto.getArea());
        project.setAddress(dto.getAddress());
        project.setClientName(dto.getClientName());
        project.setValuationTime(dto.getValuationTime());
        project.setBuildingArea(dto.getBuildingArea());
        project.setValuationType("自动估值");

        if (price.isPresent()) {
            ValuationPrice vp = price.get();
            project.setValuationUnitPrice(vp.getUnitPrice());
            project.setValuationTotalPrice(vp.getTotalPrice());
            project.setStatus("已评估");
            project.setRemark(StringUtils.hasText(dto.getRemark()) ? dto.getRemark() : "自动估值成功");
        } else {
            project.setValuationUnitPrice(null);
            project.setValuationTotalPrice(null);
            project.setStatus("未评估");
            project.setRemark("该小区无法估值");
        }

        return toDto(projectRepository.save(project));
    }


    @Override
    public ProjectDetailDto getProjectDetail(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("项目不存在"));

        ProjectDetailDto detail = new ProjectDetailDto();
        detail.setProject(toDto(project));

        // Collaterals
        List<Collateral> collaterals = collateralRepository.findByProjectId(id);
        detail.setCollaterals(collaterals.stream().map(this::toCollateralDto).collect(Collectors.toList()));

        // ValuationReports with methods and reviews
        List<ValuationReport> reports = valuationReportRepository.findByProjectId(id);
        List<Long> reportIds = reports.stream().map(ValuationReport::getId).collect(Collectors.toList());

        List<ValuationMethod> allMethods = reportIds.isEmpty()
                ? List.of()
                : valuationMethodRepository.findByReportIdIn(reportIds);
        Map<Long, List<ValuationMethodDto>> methodsByReport = allMethods.stream()
                .collect(Collectors.groupingBy(ValuationMethod::getReportId,
                        Collectors.mapping(this::toValuationMethodDto, Collectors.toList())));

        List<ReportReview> allReviews = reportIds.isEmpty()
                ? List.of()
                : reportReviewRepository.findByReportIdIn(reportIds);
        Map<Long, List<ReportReviewDto>> reviewsByReport = allReviews.stream()
                .collect(Collectors.groupingBy(ReportReview::getReportId,
                        Collectors.mapping(this::toReportReviewDto, Collectors.toList())));

        detail.setValuationReports(reports.stream().map(r -> {
            ValuationReportDto dto = toValuationReportDto(r);
            dto.setValuationMethods(methodsByReport.getOrDefault(r.getId(), List.of()));
            dto.setReportReviews(reviewsByReport.getOrDefault(r.getId(), List.of()));
            return dto;
        }).collect(Collectors.toList()));

        // Surveys with photos
        List<Survey> surveys = surveyRepository.findByProjectId(id);
        List<Long> surveyIds = surveys.stream().map(Survey::getId).collect(Collectors.toList());

        List<SurveyPhoto> allPhotos = surveyIds.isEmpty()
                ? List.of()
                : surveyPhotoRepository.findBySurveyIdIn(surveyIds);
        Map<Long, List<SurveyPhotoDto>> photosBySurvey = allPhotos.stream()
                .collect(Collectors.groupingBy(SurveyPhoto::getSurveyId,
                        Collectors.mapping(this::toSurveyPhotoDto, Collectors.toList())));

        detail.setSurveys(surveys.stream().map(s -> {
            SurveyDto dto = toSurveyDto(s);
            dto.setPhotos(photosBySurvey.getOrDefault(s.getId(), List.of()));
            return dto;
        }).collect(Collectors.toList()));

        // OwnershipInfo (single record per project)
        detail.setOwnershipInfo(ownershipInfoRepository.findByProjectId(id)
                .map(this::toOwnershipInfoDto).orElse(null));

        return detail;
    }

    private ProjectDto toDto(Project p) {
        ProjectDto dto = new ProjectDto();
        dto.setId(p.getId());
        dto.setProjectCode(p.getProjectCode());
        dto.setProjectName(p.getProjectName());
        dto.setCity(p.getCity());
        dto.setDistrict(p.getDistrict());
        dto.setArea(p.getArea());
        dto.setAddress(p.getAddress());
        dto.setRegistrar(p.getRegistrar());
        dto.setRegistrationDate(p.getRegistrationDate());
        dto.setClientName(p.getClientName());
        dto.setClientContact(p.getClientContact());
        dto.setClientPhone(p.getClientPhone());
        dto.setMortgagorName(p.getMortgagorName());
        dto.setMortgagorIdCard(p.getMortgagorIdCard());
        dto.setMortgagorPhone(p.getMortgagorPhone());
        dto.setBorrowerName(p.getBorrowerName());
        dto.setBorrowerIdCard(p.getBorrowerIdCard());
        dto.setValuationPurpose(p.getValuationPurpose());
        dto.setValuationTime(p.getValuationTime());
        dto.setExpectedPrice(p.getExpectedPrice());
        dto.setValuationUnitPrice(p.getValuationUnitPrice());
        dto.setValuationTotalPrice(p.getValuationTotalPrice());
        dto.setValuationType(p.getValuationType());
        dto.setBuildingArea(p.getBuildingArea());
        dto.setStatus(p.getStatus());
        dto.setRemark(p.getRemark());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    private CollateralDto toCollateralDto(Collateral c) {
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

    private ValuationReportDto toValuationReportDto(ValuationReport r) {
        ValuationReportDto dto = new ValuationReportDto();
        dto.setId(r.getId());
        dto.setReportCode(r.getReportCode());
        dto.setStartTime(r.getStartTime());
        dto.setEndTime(r.getEndTime());
        dto.setUnitPrice(r.getUnitPrice());
        dto.setTotalPrice(r.getTotalPrice());
        dto.setMortgageValue(r.getMortgageValue());
        dto.setPriorityCompensationAmount(r.getPriorityCompensationAmount());
        dto.setPriorityCompensationDescription(r.getPriorityCompensationDescription());
        dto.setValueDate(r.getValueDate());
        dto.setReportIssueDate(r.getReportIssueDate());
        dto.setValidStartDate(r.getValidStartDate());
        dto.setValidEndDate(r.getValidEndDate());
        dto.setValuer1Name(r.getValuer1Name());
        dto.setValuer1CertNo(r.getValuer1CertNo());
        dto.setValuer2Name(r.getValuer2Name());
        dto.setValuer2CertNo(r.getValuer2CertNo());
        dto.setValuationResult(r.getValuationResult());
        dto.setAreaEvaluation(r.getAreaEvaluation());
        dto.setSurroundingTransactions(r.getSurroundingTransactions());
        dto.setLiquidityAnalysis(r.getLiquidityAnalysis());
        dto.setFloorPlan(r.getFloorPlan());
        dto.setLandGrantDeduction(r.getLandGrantDeduction());
        dto.setDecorationNewRate(r.getDecorationNewRate());
        dto.setEquipmentNewRate(r.getEquipmentNewRate());
        dto.setReportUrl(r.getReportUrl());
        dto.setBankSuggestion(r.getBankSuggestion());
        dto.setLandPlot(r.getLandPlot());
        return dto;
    }

    private ValuationMethodDto toValuationMethodDto(ValuationMethod m) {
        ValuationMethodDto dto = new ValuationMethodDto();
        dto.setId(m.getId());
        dto.setMethodCode(m.getMethodCode());
        dto.setMethodName(m.getMethodName());
        dto.setWeight(m.getWeight());
        dto.setUnitPrice(m.getUnitPrice());
        dto.setAppraiserSignature(m.getAppraiserSignature());
        dto.setDescription(m.getDescription());
        dto.setReportId(m.getReportId());
        return dto;
    }

    private SurveyDto toSurveyDto(Survey s) {
        SurveyDto dto = new SurveyDto();
        dto.setId(s.getId());
        dto.setSurveyCode(s.getSurveyCode());
        dto.setSurveyor(s.getSurveyor());
        dto.setReceptionist(s.getReceptionist());
        dto.setReceptionistPhone(s.getReceptionistPhone());
        dto.setSurveyDate(s.getSurveyDate());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setPropertyCertVerified(s.getPropertyCertVerified());
        dto.setOwnershipDispute(s.getOwnershipDispute());
        dto.setRemark(s.getRemark());
        return dto;
    }

    private SurveyPhotoDto toSurveyPhotoDto(SurveyPhoto p) {
        SurveyPhotoDto dto = new SurveyPhotoDto();
        dto.setId(p.getId());
        dto.setSurveyId(p.getSurveyId());
        dto.setPhotoCode(p.getPhotoCode());
        dto.setPhotoPath(p.getPhotoPath());
        dto.setPhotoCategory(p.getPhotoCategory());
        dto.setPhotoDescription(p.getPhotoDescription());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    private OwnershipInfoDto toOwnershipInfoDto(OwnershipInfo o) {
        OwnershipInfoDto dto = new OwnershipInfoDto();
        dto.setId(o.getId());
        dto.setRightHolder(o.getRightHolder());
        dto.setRightCertificateNumber(o.getRightCertificateNumber());
        dto.setRegisteredAddress(o.getRegisteredAddress());
        dto.setBorrowerName(o.getBorrowerName());
        dto.setBorrowerIdCard(o.getBorrowerIdCard());
        dto.setBuildingStructure(o.getBuildingStructure());
        dto.setUsage(o.getUsage());
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

    private ReportReviewDto toReportReviewDto(ReportReview r) {
        ReportReviewDto dto = new ReportReviewDto();
        dto.setId(r.getId());
        dto.setReportId(r.getReportId());
        dto.setReviewer(r.getReviewer());
        dto.setReviewDate(r.getReviewDate());
        dto.setReviewOpinion(r.getReviewOpinion());
        dto.setReviewResult(r.getReviewResult());
        return dto;
    }

    @Override
    @Transactional
    public void saveProjectDetail(Long id, ProjectDetailDto detail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("项目不存在"));

        // Update project fields
        ProjectDto pd = detail.getProject();
        project.setProjectName(pd.getProjectName());
        project.setCity(pd.getCity());
        project.setDistrict(pd.getDistrict());
        project.setArea(pd.getArea());
        project.setAddress(pd.getAddress());
        project.setRegistrar(pd.getRegistrar());
        project.setRegistrationDate(pd.getRegistrationDate());
        project.setClientName(pd.getClientName());
        project.setClientContact(pd.getClientContact());
        project.setClientPhone(pd.getClientPhone());
        project.setMortgagorName(pd.getMortgagorName());
        project.setMortgagorIdCard(pd.getMortgagorIdCard());
        project.setMortgagorPhone(pd.getMortgagorPhone());
        project.setBorrowerName(pd.getBorrowerName());
        project.setBorrowerIdCard(pd.getBorrowerIdCard());
        project.setValuationPurpose(pd.getValuationPurpose());
        project.setValuationTime(pd.getValuationTime());
        project.setExpectedPrice(pd.getExpectedPrice());
        project.setValuationUnitPrice(pd.getValuationUnitPrice());
        project.setValuationTotalPrice(pd.getValuationTotalPrice());
        project.setValuationType(pd.getValuationType());
        project.setBuildingArea(pd.getBuildingArea());
        project.setStatus(pd.getStatus());
        project.setRemark(pd.getRemark());
        projectRepository.save(project);

        // Replace collaterals
        collateralRepository.deleteByProjectId(id);
        if (detail.getCollaterals() != null) {
            List<Collateral> collaterals = new ArrayList<>();
            for (CollateralDto dto : detail.getCollaterals()) {
                collaterals.add(toCollateralEntity(dto, id));
            }
            collateralRepository.saveAll(collaterals);
        }

        // Replace valuation reports with methods and reviews
        List<ValuationReport> oldReports = valuationReportRepository.findByProjectId(id);
        List<Long> oldReportIds = oldReports.stream().map(ValuationReport::getId).collect(Collectors.toList());
        if (!oldReportIds.isEmpty()) {
            reportReviewRepository.deleteByReportIdIn(oldReportIds);
            valuationMethodRepository.deleteByReportIdIn(oldReportIds);
        }
        valuationReportRepository.deleteByProjectId(id);

        if (detail.getValuationReports() != null) {
            for (ValuationReportDto rd : detail.getValuationReports()) {
                ValuationReport report = toValuationReportEntity(rd, id);
                report = valuationReportRepository.save(report);

                if (rd.getValuationMethods() != null) {
                    for (ValuationMethodDto md : rd.getValuationMethods()) {
                        ValuationMethod method = toValuationMethodEntity(md, report.getId());
                        valuationMethodRepository.save(method);
                    }
                }
                if (rd.getReportReviews() != null) {
                    for (ReportReviewDto rrd : rd.getReportReviews()) {
                        ReportReview review = toReportReviewEntity(rrd, id, report.getId());
                        reportReviewRepository.save(review);
                    }
                }
            }
        }

        // Replace surveys with photos
        List<Survey> oldSurveys = surveyRepository.findByProjectId(id);
        List<Long> oldSurveyIds = oldSurveys.stream().map(Survey::getId).collect(Collectors.toList());
        if (!oldSurveyIds.isEmpty()) {
            surveyPhotoRepository.deleteBySurveyIdIn(oldSurveyIds);
        }
        surveyRepository.deleteByProjectId(id);

        if (detail.getSurveys() != null) {
            for (SurveyDto sd : detail.getSurveys()) {
                Survey survey = toSurveyEntity(sd, id);
                survey = surveyRepository.save(survey);

                if (sd.getPhotos() != null) {
                    for (SurveyPhotoDto pd2 : sd.getPhotos()) {
                        SurveyPhoto photo = toSurveyPhotoEntity(pd2, id, survey.getId());
                        surveyPhotoRepository.save(photo);
                    }
                }
            }
        }

        // Save ownership info (single record)
        if (detail.getOwnershipInfo() != null) {
            OwnershipInfo o = ownershipInfoRepository.findByProjectId(id)
                    .orElseGet(() -> {
                        OwnershipInfo newEntity = new OwnershipInfo();
                        newEntity.setProjectId(id);
                        return newEntity;
                    });
            applyOwnershipDto(o, detail.getOwnershipInfo());
            ownershipInfoRepository.save(o);
        }
    }

    // --- DTO -> Entity converters ---

    private Collateral toCollateralEntity(CollateralDto dto, Long projectId) {
        Collateral c = new Collateral();
        c.setProjectId(projectId);
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
        return c;
    }

    private ValuationReport toValuationReportEntity(ValuationReportDto dto, Long projectId) {
        ValuationReport r = new ValuationReport();
        r.setProjectId(projectId);
        r.setReportCode(dto.getReportCode());
        r.setStartTime(dto.getStartTime());
        r.setEndTime(dto.getEndTime());
        r.setUnitPrice(dto.getUnitPrice());
        r.setTotalPrice(dto.getTotalPrice());
        r.setMortgageValue(dto.getMortgageValue());
        r.setPriorityCompensationAmount(dto.getPriorityCompensationAmount());
        r.setPriorityCompensationDescription(dto.getPriorityCompensationDescription());
        r.setValueDate(dto.getValueDate());
        r.setReportIssueDate(dto.getReportIssueDate());
        r.setValidStartDate(dto.getValidStartDate());
        r.setValidEndDate(dto.getValidEndDate());
        r.setValuer1Name(dto.getValuer1Name());
        r.setValuer1CertNo(dto.getValuer1CertNo());
        r.setValuer2Name(dto.getValuer2Name());
        r.setValuer2CertNo(dto.getValuer2CertNo());
        r.setValuationResult(dto.getValuationResult());
        r.setAreaEvaluation(dto.getAreaEvaluation());
        r.setSurroundingTransactions(dto.getSurroundingTransactions());
        r.setLiquidityAnalysis(dto.getLiquidityAnalysis());
        r.setFloorPlan(dto.getFloorPlan());
        r.setLandGrantDeduction(dto.getLandGrantDeduction());
        r.setDecorationNewRate(dto.getDecorationNewRate());
        r.setEquipmentNewRate(dto.getEquipmentNewRate());
        r.setReportUrl(dto.getReportUrl());
        r.setBankSuggestion(dto.getBankSuggestion());
        r.setLandPlot(dto.getLandPlot());
        return r;
    }

    private ValuationMethod toValuationMethodEntity(ValuationMethodDto dto, Long reportId) {
        ValuationMethod m = new ValuationMethod();
        m.setMethodCode(dto.getMethodCode());
        m.setMethodName(dto.getMethodName());
        m.setWeight(dto.getWeight());
        m.setUnitPrice(dto.getUnitPrice());
        m.setAppraiserSignature(dto.getAppraiserSignature());
        m.setDescription(dto.getDescription());
        m.setReportId(reportId);
        return m;
    }

    private Survey toSurveyEntity(SurveyDto dto, Long projectId) {
        Survey s = new Survey();
        s.setProjectId(projectId);
        s.setSurveyCode(dto.getSurveyCode());
        s.setSurveyor(dto.getSurveyor());
        s.setReceptionist(dto.getReceptionist());
        s.setReceptionistPhone(dto.getReceptionistPhone());
        s.setSurveyDate(dto.getSurveyDate());
        s.setStartTime(dto.getStartTime());
        s.setEndTime(dto.getEndTime());
        s.setPropertyCertVerified(dto.getPropertyCertVerified());
        s.setOwnershipDispute(dto.getOwnershipDispute());
        s.setRemark(dto.getRemark());
        return s;
    }

    private SurveyPhoto toSurveyPhotoEntity(SurveyPhotoDto dto, Long projectId, Long surveyId) {
        SurveyPhoto p = new SurveyPhoto();
        p.setProjectId(projectId);
        p.setSurveyId(surveyId);
        p.setPhotoCode(dto.getPhotoCode());
        p.setPhotoPath(dto.getPhotoPath());
        p.setPhotoCategory(dto.getPhotoCategory());
        p.setPhotoDescription(dto.getPhotoDescription());
        return p;
    }

    private void applyOwnershipDto(OwnershipInfo o, OwnershipInfoDto dto) {
        o.setRightHolder(dto.getRightHolder());
        o.setRightCertificateNumber(dto.getRightCertificateNumber());
        o.setRegisteredAddress(dto.getRegisteredAddress());
        o.setBorrowerName(dto.getBorrowerName());
        o.setBorrowerIdCard(dto.getBorrowerIdCard());
        o.setBuildingStructure(dto.getBuildingStructure());
        o.setUsage(dto.getUsage());
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

    private ReportReview toReportReviewEntity(ReportReviewDto dto, Long projectId, Long reportId) {
        ReportReview r = new ReportReview();
        r.setProjectId(projectId);
        r.setReportId(reportId);
        r.setReviewer(dto.getReviewer());
        r.setReviewDate(dto.getReviewDate());
        r.setReviewOpinion(dto.getReviewOpinion());
        r.setReviewResult(dto.getReviewResult());
        return r;
    }

    @Override
    public java.util.Map<String, Object> queryValuationPrice(String city, String address, String valuationTime) {
        if (!StringUtils.hasText(address)) {
            throw new BusinessException("地址为空，无法自动估值");
        }

        LocalDate parsedValuationTime = null;
        if (StringUtils.hasText(valuationTime)) {
            try {
                parsedValuationTime = LocalDate.parse(valuationTime.substring(0, 10));
            } catch (Exception ignored) {}
        }

        ValuationPrice vp = findValuationPrice(city, address, parsedValuationTime)
                .orElseThrow(() -> new BusinessException("未找到匹配的估值价格数据，地址: " + address));

        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("valuationUnitPrice", vp.getUnitPrice());
        result.put("valuationTotalPrice", vp.getTotalPrice());
        return result;
    }
}
