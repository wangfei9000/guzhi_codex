package com.admin.system.service;

import com.admin.system.entity.*;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.*;
import com.lowagie.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final ValuationReportRepository valuationReportRepository;
    private final ProjectRepository projectRepository;
    private final CollateralRepository collateralRepository;
    private final ValuationMethodRepository valuationMethodRepository;
    private final OwnershipInfoRepository ownershipInfoRepository;
    private final SurveyPhotoRepository surveyPhotoRepository;
    private final SurveyRepository surveyRepository;
    private final OrganizationRepository organizationRepository;
    private final ReportTemplateRepository reportTemplateRepository;

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final DateTimeFormatter CN_DATE = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    // 估价机构默认信息
    private static final String DEFAULT_ORG_NAME = "浙江德衡房地产土地资产评估有限公司";
    private static final String DEFAULT_ORG_ADDRESS = "浙江省杭州市上城区四季青街道钱塘航空大厦2幢1510室";
    private static final String DEFAULT_ORG_PHONE = "0571-88060768";
    private static final String DEFAULT_ORG_EMAIL = "124400690@qq.com";
    private static final String DEFAULT_CLIENT_NAME = "北京银行股份有限公司杭州分行";
    private static final String DEFAULT_LEGAL_REPRESENTATIVE = "韩松";
    private static final String DEFAULT_UNIFIED_SOCIAL_CREDIT_CODE = "91331001768682598N";
    private static final String DEFAULT_FIRST_RECORD_DATE = "2005-03-02";
    private static final String DEFAULT_RECORD_CERTIFICATE_NO = "浙建房估证字[2005]002号";
    private static final String DEFAULT_RECORD_CERTIFICATE_VALID_PERIOD = "2024年04月29日至2027年03月07日";
    private static final String DEFAULT_CONTACT_NAME = "金德敏";
    private static final String DEFAULT_VALUER1_NAME = "韩松";
    private static final String DEFAULT_VALUER1_CERT_NO = "6120050037";
    private static final String DEFAULT_VALUER2_NAME = "王义贵";
    private static final String DEFAULT_VALUER2_CERT_NO = "3320180164";

    public String generatePdf(Long reportId) throws Exception {
        ValuationReport report = valuationReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("估价报告不存在"));

        Project project = projectRepository.findById(report.getProjectId())
                .orElseThrow(() -> new RuntimeException("项目不存在"));

        LocalDateTime finishedAt = LocalDateTime.now();
        if (report.getStartTime() == null) {
            report.setStartTime(finishedAt);
        }
        report.setEndTime(finishedAt);

        List<Collateral> collaterals = collateralRepository.findByProjectId(project.getId());
        List<ValuationMethod> methods = valuationMethodRepository.findByReportId(reportId);
        OwnershipInfo ownership = ownershipInfoRepository.findByProjectId(project.getId()).orElse(null);
        List<Survey> surveys = surveyRepository.findByProjectId(project.getId());

        // 加载项目所有勘查照片
        List<SurveyPhoto> allPhotos = surveyPhotoRepository.findByProjectId(project.getId());

        String template = loadTemplateFromOrganization(project);

        // Build data model and process FreeMarker template
        Map<String, Object> dataModel = buildDataModel(report, project, collaterals, methods, ownership, surveys, allPhotos);
        System.out.println("---------------------------------------\n"+dataModel);
        String html = processFreeMarker(template, dataModel);

        // Generate PDF with CJK font support
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        boolean fontRegistered = registerBundledFont(renderer, "fonts/Arial Unicode.ttf");
        if (!fontRegistered) {
            throw new RuntimeException("CJK font not found: fonts/Arial Unicode.ttf. " +
                    "Please ensure the font file is present in src/main/resources/fonts/");
        }

        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(baos);
        baos.close();

        // Save PDF file
        String pdfDir = project.getProjectCode();
        Path pdfPath = Paths.get(uploadDir, pdfDir);
        Files.createDirectories(pdfPath);
        String pdfName = "report-" + report.getReportCode() + "-" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
        Path targetPath = pdfPath.resolve(pdfName);
        Files.write(targetPath, baos.toByteArray());

        // Update report URL
        String filePath = pdfDir + "/" + pdfName;
        report.setReportUrl(filePath);
        valuationReportRepository.save(report);

        log.info("PDF generated: {}", filePath);
        return filePath;
    }

    private String loadTemplateFromOrganization(Project project) {
        String clientName = project.getClientName();
        if (!StringUtils.hasText(clientName)) {
            throw new BusinessException("项目未设置委托单位，无法匹配报告模版");
        }

        Organization organization = organizationRepository.findFirstByOrganizationName(clientName.trim())
                .orElseThrow(() -> new BusinessException("委托单位未配置机构信息，无法匹配报告模版"));

        Long reportTemplateId = organization.getReportTemplateId();
        if (reportTemplateId == null) {
            throw new BusinessException("机构未绑定报告模版");
        }

        ReportTemplate reportTemplate = reportTemplateRepository.findById(reportTemplateId)
                .orElseThrow(() -> new BusinessException("机构绑定的报告模版不存在"));

        if (!StringUtils.hasText(reportTemplate.getTemplateContent())) {
            throw new BusinessException("机构绑定的报告模版内容为空");
        }

        log.info("Loading report template from database: organization={}, templateId={}",
                organization.getOrganizationName(), reportTemplateId);
        return reportTemplate.getTemplateContent();
    }

    /**
     * Build the FreeMarker data model from all available entities.
     */
    private Map<String, Object> buildDataModel(ValuationReport report, Project project,
                                                List<Collateral> collaterals, List<ValuationMethod> methods,
                                                OwnershipInfo o, List<Survey> surveys, List<SurveyPhoto> allPhotos) {
        Map<String, Object> data = new HashMap<>();
        LocalDate today = LocalDate.now();
        String todayCn = today.format(CN_DATE);
        Collateral primaryCollateral = collaterals.stream()
                .filter(c -> Boolean.TRUE.equals(c.getPrimaryCollateral()))
                .findFirst()
                .orElse(collaterals.isEmpty() ? null : collaterals.get(0));

        // ========== 封面信息 ==========
        data.put("reportTitle", "房 地 产 抵 押 估 价 报 告");
        data.put("projectName", nvl(project.getProjectName()));
        data.put("reportNo", nvl(report.getReportCode()));

        // ========== 估价机构信息 ==========
        // data.put("valuationOrg", DEFAULT_ORG_NAME);
        // data.put("orgAddress", DEFAULT_ORG_ADDRESS);
        // data.put("orgPhone", DEFAULT_ORG_PHONE);
        // data.put("orgEmail", DEFAULT_ORG_EMAIL);
        data.put("legalRepresentative", DEFAULT_LEGAL_REPRESENTATIVE);
        data.put("unifiedSocialCreditCode", DEFAULT_UNIFIED_SOCIAL_CREDIT_CODE);
        // data.put("organizationForm", "有限责任公司（自然人投资或控股）");
        data.put("firstRecordDate", DEFAULT_FIRST_RECORD_DATE);
        // data.put("recordLevel", "一级");
        data.put("recordCertificateNo", DEFAULT_RECORD_CERTIFICATE_NO);
        data.put("recordCertificateValidPeriod", DEFAULT_RECORD_CERTIFICATE_VALID_PERIOD);
        data.put("contactName", DEFAULT_CONTACT_NAME);

        // ========== 委托人信息 ==========
        data.put("clientName", defaultIfBlank(project.getClientName(), DEFAULT_CLIENT_NAME));
        data.put("clientorInput", nvl(project.getClientContact()));

        // ========== 估价目的 / 价值类型 ==========
        data.put("valuationPurpose", nvl(project.getValuationPurpose()));
        // data.put("valueType", "抵押价值");

        // ========== 估价对象 — 地址 ==========
        String propertyAddress = firstNonBlank(
                primaryCollateral != null ? primaryCollateral.getCollateralAddress() : null,
                o != null ? o.getRegisteredAddress() : null,
                project.getAddress()
        );
        data.put("propertyAddress", propertyAddress);
        data.put("position", propertyAddress);
        data.put("positionSplit", propertyAddress);
        data.put("city", nvl(project.getCity()));
        data.put("district", nvl(project.getDistrict()));
        data.put("address", propertyAddress);

        // ========== 权属信息（来自 ownership_info 表） ==========
        String ownerName = firstNonBlank(o != null ? o.getRightHolder() : null, project.getMortgagorName());
        data.put("ownerName", ownerName);
        data.put("landUseRightOwner", ownerName);
        data.put("propertyCertificateNo", firstNonBlank(
                o != null ? o.getHouseOwnershipCertificate() : null,
                o != null ? o.getRightCertificateNumber() : null
        ));
        data.put("landCertificateNo", nvl(o != null ? o.getStateLandUseCertificateNumber() : null));
        data.put("buildingStructure", nvl(o != null ? o.getBuildingStructure() : null));
        data.put("rightNature", nvl(o != null ? o.getRightNature() : null));
        data.put("landRightNature", nvl(o != null ? o.getRightNature() : null));
        data.put("coOwnership", nvl(o != null ? o.getCoOwnership() : null));
        data.put("floorName", firstNonBlank(
                primaryCollateral != null ? primaryCollateral.getCurrentFloor() : null,
                o != null ? o.getCurrentFloor() : null
        ));

        // 用途
        String usage = o != null ? o.getUsage() : null;
        String landUse = o != null ? o.getLandUse() : null;
        data.put("registeredUse", combineUse(landUse, usage));
        data.put("actualUse", defaultIfBlank(firstNonBlank(
                o != null ? o.getActualUse() : null,
                primaryCollateral != null ? primaryCollateral.getActualUse() : null,
                usage
        ), "住宅"));
        data.put("landActualUse", nvl(o != null ? o.getLandUse() : null));

        // 楼层
        data.put("totalFloor", firstNonBlank(
                fmt(o != null ? o.getTotalFloors() : null),
                fmt(primaryCollateral != null ? primaryCollateral.getFloorCount() : null)
        ));

        // 建成年代
        data.put("builtYear", firstNonBlank(
                fmt(o != null ? o.getBuildYear() : null),
                fmt(primaryCollateral != null ? primaryCollateral.getBuildYear() : null)
        ));

        // ========== 面积信息 ==========
        BigDecimal buildingArea = primaryCollateral != null ? primaryCollateral.getBuildingArea() : null;
        if (buildingArea == null && o != null) {
            buildingArea = o.getRegisteredBuildingArea();
        }
        if (buildingArea == null) {
            buildingArea = project.getBuildingArea();
        }
        BigDecimal landUseArea = o != null ? o.getLandUseArea() : null;
        if (landUseArea == null && o != null) {
            landUseArea = o.getAllocatedLandArea() != null ? o.getAllocatedLandArea() : o.getSharedLandArea();
        }
        if (landUseArea == null && primaryCollateral != null) {
            landUseArea = primaryCollateral.getLandArea();
        }

        data.put("buildingArea", fmt(buildingArea));
        data.put("area", fmt(buildingArea));
        data.put("landUseRightArea", fmt(landUseArea));
        data.put("housingAreaInput", fmt(buildingArea));

        // ========== 土地终止日期 & 剩余年限 ==========
        Integer landUseYears = o != null ? o.getLandUseYears() : null;
        String landEndDate = "";
        String remainingLandYears = "";
        if (o != null && o.getLandUseEndDate() != null) {
            landEndDate = o.getLandUseEndDate().format(CN_DATE);
            remainingLandYears = String.valueOf(Math.max(0, o.getLandUseEndDate().getYear() - today.getYear()));
        } else if (landUseYears != null && landUseYears > 0) {
            int builtYear = o != null && o.getBuildYear() != null ? o.getBuildYear() : today.getYear();
            int endYear = builtYear + landUseYears;
            landEndDate = endYear + "年";
            int remaining = endYear - today.getYear();
            remainingLandYears = String.valueOf(Math.max(0, remaining));
        }
        data.put("landEndDate", landEndDate);
        data.put("remainingLandYears", remainingLandYears);

        // ========== 价值时点（优先使用报告批次，其次权属信息、项目） ==========
        LocalDate valuationTimePoint = report.getValueDate();
        if (valuationTimePoint == null && o != null) valuationTimePoint = o.getValuationTimePoint();
        if (valuationTimePoint == null) valuationTimePoint = project.getValuationTime();
        if (valuationTimePoint == null) valuationTimePoint = today;

        data.put("valueDate", valuationTimePoint.format(CN_DATE));
        data.put("valueDateCn", toChineseDate(valuationTimePoint));
        data.put("priceTime", nvl(project.getValuationTime()));

        // ========== 报告出具日期（优先使用报告批次，其次权属信息） ==========
        LocalDate reportIssueDate = report.getReportIssueDate();
        if (reportIssueDate == null && o != null) reportIssueDate = o.getReportIssueDate();
        if (reportIssueDate == null) reportIssueDate = today;

        data.put("issueDate", reportIssueDate.format(CN_DATE));
        data.put("issueDateCn", toChineseDate(reportIssueDate));
        data.put("today", todayCn);

        // ========== 报告有效期 ==========
        LocalDate validStartDate = report.getValidStartDate() != null ? report.getValidStartDate() : reportIssueDate;
        LocalDate validEndDate = report.getValidEndDate() != null
                ? report.getValidEndDate()
                : reportIssueDate.plusYears(1).minusDays(1);
        data.put("validStartDate", validStartDate.format(CN_DATE));
        data.put("validEndDate", validEndDate.format(CN_DATE));

        // ========== 估价作业日期 ==========
        String workStart = report.getStartTime() != null
                ? report.getStartTime().toLocalDate().format(CN_DATE) : "";
        String workEnd = report.getEndTime() != null
                ? report.getEndTime().toLocalDate().format(CN_DATE) : "";
        data.put("workStartDateCn", workStart);
        data.put("workEndDateCn", workEnd);
        data.put("startTime", nvl(report.getStartTime()));
        data.put("endTime", nvl(report.getEndTime()));

        // ========== 实地查勘日期 ==========
        LocalDate siteInspectionDate = surveys.stream()
                .map(Survey::getSurveyDate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(valuationTimePoint);
        String surveyorNames = joinNonBlank(surveys.stream().map(Survey::getSurveyor).toList());
        data.put("siteInspectionDate", siteInspectionDate.format(CN_DATE));
        data.put("siteInspectionDateCn", toChineseDate(siteInspectionDate));
        data.put("siteInspectorNames", surveyorNames);

        // ========== 估价方法信息 ==========
        String methodName = "";
        String appraiserSignatures = "";
        if (!methods.isEmpty()) {
            ValuationMethod m = methods.get(0);
            methodName = nvl(m.getMethodName());
            appraiserSignatures = nvl(m.getAppraiserSignature());
        }
        data.put("valuationMethod", methodName.isEmpty() ? "比较法" : methodName);
        data.put("methodName", methodName);
        data.put("appraiserSignatures", appraiserSignatures);

        // 不采用收益法/成本法原因
        // data.put("incomeApproachReason", "根据评估对象的利用类型、评估基准日与所在区域房地产市场情况，考虑到评估对象实际用途为住宅房产，周边租赁市场较不活跃，未来预期收益和风险不可以被预测并衡量，房产获利年限不可以被预测，故不选用收益法对评估对象进行评估。");
        // data.put("costApproachReason", "估价对象为住宅房地产，成本法所需的土地取得成本、建筑物建造成本等数据较难取得，且估价对象区域内多以住宅为主，与估价对象的形态较为匹配，目前使用状况良好，无需重新开发或再开发，故不宜采用成本法及假设开发法进行评估。");

        // ========== 估价师信息 ==========
        List<String> appraiserNames = splitNames(appraiserSignatures);
        String valuer1Name = firstNonBlank(
                report.getValuer1Name(),
                appraiserNames.size() > 0 ? appraiserNames.get(0) : null,
                DEFAULT_VALUER1_NAME
        );
        String valuer2Name = firstNonBlank(
                report.getValuer2Name(),
                appraiserNames.size() > 1 ? appraiserNames.get(1) : null,
                DEFAULT_VALUER2_NAME
        );
        String valuer1CertNo = firstNonBlank(report.getValuer1CertNo(), DEFAULT_VALUER1_CERT_NO);
        String valuer2CertNo = firstNonBlank(report.getValuer2CertNo(), DEFAULT_VALUER2_CERT_NO);
        data.put("valuer1Name", valuer1Name);
        data.put("valuer1CertNo", valuer1CertNo);
        data.put("valuer2Name", valuer2Name);
        data.put("valuer2CertNo", valuer2CertNo);
        data.put("valuer1Name1", valuer1Name);
        data.put("valuer1CertNo1", valuer1CertNo);
        data.put("valuer2Name1", valuer2Name);
        data.put("valuer2CertNo1", valuer2CertNo);
        if (surveyorNames.isBlank()) {
            data.put("siteInspectorNames", valuer1Name + "、" + valuer2Name);
        }

        // ========== 价格信息 ==========
        BigDecimal unitPrice = report.getUnitPrice() != null ? report.getUnitPrice() : project.getValuationUnitPrice();
        data.put("referenceUnitPrice", unitPrice != null ? unitPrice.toPlainString() : "");
        data.put("trendPrice", unitPrice != null ? unitPrice.toPlainString() : "");

        BigDecimal estimatedTotalYuan = null;
        if (unitPrice != null && buildingArea != null) {
            estimatedTotalYuan = unitPrice.multiply(buildingArea);
        }

        // 估值总价（万元）：正式报告优先取报告批次，项目估值只作为缓存兜底
        BigDecimal totalPrice = report.getTotalPrice() != null ? report.getTotalPrice() : project.getValuationTotalPrice();
        if (totalPrice == null && estimatedTotalYuan != null) {
            totalPrice = estimatedTotalYuan.divide(new BigDecimal("10000"), 2, java.math.RoundingMode.HALF_UP);
        }
        String totalPriceStr = totalPrice != null ? totalPrice.toPlainString() : "";
        data.put("referenceTotalPrice", totalPriceStr);

        BigDecimal mortgageValue = report.getMortgageValue() != null ? report.getMortgageValue() : totalPrice;
        data.put("mortgageValue", mortgageValue != null ? mortgageValue.toPlainString() : "");

        // 评估总价（元）= 总价万元 × 10000
        String totalPriceYuan = totalPrice != null
                ? totalPrice.multiply(new BigDecimal("10000")).toPlainString() : "";
        data.put("referenceTotalPriceYuan", totalPriceYuan);

        // 期望价格（兼容旧模板）
        data.put("expectedPrice", nvl(project.getExpectedPrice()));

        // 大写金额（占位）
        data.put("capitalamount", mortgageValue != null
                ? toRmbUpper(mortgageValue.multiply(new BigDecimal("10000")))
                : "");

        // 估价总价值（单价×面积）
        String estimatedTotalValueYuan = estimatedTotalYuan != null ? estimatedTotalYuan.toPlainString() : "";
        data.put("estimatedTotalValueYuan", estimatedTotalValueYuan);

        // 法定优先受偿款
        BigDecimal priorityCompensationAmount = report.getPriorityCompensationAmount() != null
                ? report.getPriorityCompensationAmount()
                : BigDecimal.ZERO;
        data.put("priorityCompensationAmount", priorityCompensationAmount.toPlainString());
        data.put("priorityCompensationDescription", defaultIfBlank(
                report.getPriorityCompensationDescription(),
                "应委托方要求，本次估价不考虑估价对象他项权利，为完全权利状况下的市场价值，故本次估价法定优先受偿款款项为" + priorityCompensationAmount.toPlainString() + "元。"
        ));

        // ========== 抵押物信息（来自 collateral 表） ==========
        if (!collaterals.isEmpty()) {
            Collateral c = collaterals.get(0);
            data.put("communityName", nvl(c.getCommunityName()));
            data.put("useDuration", fmt(c.getLandUseYears()));
            data.put("completionDate", nvl(c.getCompletionDate()));
            // 如果建筑面积还是空，用抵押物的
            if (buildingArea == null) {
                data.put("buildingArea", fmt(c.getBuildingArea()));
                data.put("area", fmt(c.getBuildingArea()));
            }
            // 装修成新率（来自估价报告）
            data.put("decoration", defaultIfBlank(firstNonBlank(
                    o != null ? o.getDecoration() : null,
                    c.getDecoration()
            ), "一般装修"));
        }

        // 抵押物表格行
        StringBuilder collateralRows = new StringBuilder();
        for (Collateral c : collaterals) {
            collateralRows.append("<table>")
                    .append("<tr><td class=\"label\">抵押物编号</td><td class=\"value\">").append(nvl(c.getCollateralCode())).append("</td>")
                    .append("<td class=\"label\">抵押物类型</td><td class=\"value\">").append(nvl(c.getCollateralType())).append("</td></tr>")
                    .append("<tr><td class=\"label\">抵押物名称</td><td class=\"value\">").append(nvl(c.getCollateralName())).append("</td>")
                    .append("<td class=\"label\">抵押物地址</td><td class=\"value\">").append(nvl(c.getCollateralAddress())).append("</td></tr>")
                    .append("<tr><td class=\"label\">建筑面积</td><td class=\"value\">").append(fmt(c.getBuildingArea())).append("</td>")
                    .append("<td class=\"label\">土地面积</td><td class=\"value\">").append(fmt(c.getLandArea())).append("</td></tr>")
                    .append("<tr><td class=\"label\">小区名称</td><td class=\"value\">").append(nvl(c.getCommunityName())).append("</td>")
                    .append("<td class=\"label\">竣工日期</td><td class=\"value\">").append(nvl(c.getCompletionDate())).append("</td></tr>")
                    .append("</table>");
        }
        if (collaterals.isEmpty()) {
            collateralRows.append("<p>无抵押物信息</p>");
        }
        data.put("collateralRows", collateralRows.toString());

        // ========== 估价方法表格行 ==========
        if (!methods.isEmpty()) {
            ValuationMethod m = methods.get(0);
            data.put("methodWeight", fmt(m.getWeight()));
        }

        StringBuilder methodRows = new StringBuilder();
        if (!methods.isEmpty()) {
            methodRows.append("<table>")
                    .append("<tr><th>方法名称</th><th>权重</th><th>单价</th><th>估价师</th><th>描述</th></tr>");
            for (ValuationMethod m : methods) {
                methodRows.append("<tr>")
                        .append("<td>").append(nvl(m.getMethodName())).append("</td>")
                        .append("<td>").append(fmt(m.getWeight())).append("</td>")
                        .append("<td>").append(m.getUnitPrice() != null ? "¥" + m.getUnitPrice().toPlainString() : "-").append("</td>")
                        .append("<td>").append(nvl(m.getAppraiserSignature())).append("</td>")
                        .append("<td>").append(nvl(m.getDescription())).append("</td>")
                        .append("</tr>");
            }
            methodRows.append("</table>");
        } else {
            methodRows.append("<p>无估价方法</p>");
        }
        data.put("methodRows", methodRows.toString());

        // ========== 估价报告字段 ==========
        data.put("valuationResult", nvl(report.getValuationResult()));
        data.put("areaEvaluation", nvl(report.getAreaEvaluation()));
        data.put("surroundingTransactions", nvl(report.getSurroundingTransactions()));
        data.put("liquidityAnalysis", nvl(report.getLiquidityAnalysis()));
        data.put("floorPlan", nvl(report.getFloorPlan()));
        data.put("landGrantDeduction", report.getLandGrantDeduction() != null ? report.getLandGrantDeduction().toPlainString() : "");
        data.put("decorationNewRate", fmt(report.getDecorationNewRate()));
        data.put("equipmentNewRate", fmt(report.getEquipmentNewRate()));
        data.put("landPlot", nvl(report.getLandPlot()));
        data.put("bankSuggestion", nvl(report.getBankSuggestion()));

        // ========== 实物状况描述（带默认值） ==========
        // data.put("assetType", "房地产类（居住用房）");
        data.put("occupancyStatus", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getOccupancyStatus() : null, "自住"));
        data.put("parcelShape", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getParcelShape() : null, "形状较规则，便于建筑物布置。"));
        data.put("terrain", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getTerrain() : null, "地势平坦，无明显的坡度。"));
        data.put("landLevel", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getLandLevel() : null, "该宗地与相邻土地、道路基本齐平。"));
        data.put("soilCondition", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getSoilCondition() : null, "该宗地为城镇住宅用地，没有迹象表明土壤受过影响。"));
        data.put("landDevelopmentLevel", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getLandDevelopmentLevel() : null, "到价值时点，该宗地红线外基础设施达到“六通”（即通路、供电、供水、排水、通讯、通气）。"));
        data.put("landscape", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getLandscape() : null, "建筑物间距适中，景观质量较好。"));
        data.put("surroundingEnvironment", nvl(primaryCollateral != null ? primaryCollateral.getSurroundingEnvironment() : null));
        data.put("facilitiesCondition", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getFacilitiesCondition() : null, "估价对象设备齐全，总体维护较好。"));
        data.put("spaceLayout", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getSpaceLayout() : null, "阳台、厨房、卧室、卫生间。"));
        String decoration = firstNonBlank(
                o != null ? o.getDecoration() : null,
                primaryCollateral != null ? primaryCollateral.getDecoration() : null
        );
        data.put("decorationDescription", defaultIfBlank(decoration,
                "建筑物为现代风格，外观一般。至价值时点，估价对象整体装修良好，房屋通风、采光较好。"));
        data.put("indoorHeight", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getIndoorHeight() : null, "标准层高"));
        data.put("orientation", nvl(primaryCollateral != null ? primaryCollateral.getOrientation() : null));
        data.put("maintenanceCondition", defaultIfBlank(primaryCollateral != null ? primaryCollateral.getMaintenanceCondition() : null,
                "现场勘察时没有发现不均匀沉降，建筑物结构构件较好，装修和设备较好、齐全完整，管道畅通，地面、墙面、门窗保养维护正常。房屋属于完好房。"));

        // ========== 他项权利 ==========
        String otherRightsSummary = buildOtherRightsSummary(o);
        data.put("otherRightsStatus", defaultIfBlank(otherRightsSummary,
                "从估价人员知悉的情况看，截止估价基准日，根据估价委托人要求，评估结果不考虑抵押、担保等他项权利，为估价对象于价值时点完全产权房地产市场价格。"));
        data.put("otherRightsBrief", defaultIfBlank(otherRightsSummary,
                "未设定他项权利，现为自住，无出租或占用情况，无其他特殊情况。"));

        // ========== 区位描述 ==========
        String addr = project.getAddress() != null ? project.getAddress() : "";
        data.put("locationDescription", "估价对象坐落于" + addr + "，区位优势明显。");
        // data.put("trafficDescription", "");
        data.put("environmentDescription", "估价对象所在区域自然环境和人文环境均较好，周边居住氛围较浓厚，周边商业氛围较好。");
        // data.put("publicFacilitiesDescription", "");

        // ========== 市场背景分析 ==========
        data.put("landMarketAnalysis", "2025年以来，杭州土地市场成交方面，核心区商住用地竞争热度较高，远郊区域市场分化明显。");
        data.put("realEstateMarketAnalysis", "杭州住宅房地产市场呈现核心抗跌、外围调整的特征，二手房刚需房源主导，部分板块挂牌价持续回调；新房市场结构性分化，核心区高端盘价格相对坚挺，外围区域去化压力显现。");
        data.put("marketOutlook", "后续杭州住宅房地产市场预计延续结构性分化，核心区域抗跌性较强，外围区域仍需关注库存及去化压力；商办市场核心区有望稳中提升，新兴板块空置率可能继续攀升。");
        // data.put("marketBackgroundHtml", "");

        // ========== 变现能力分析 ==========
        // data.put("marketabilityGeneralUse", "本次估价对象为住宅房地产，通用性较好。");
        // data.put("marketabilityIndependentUse", "本次估价对象的产权状况明晰，利用现状符合法定用途及规划限制条件，目前尚无影响其独立使用的因素存在，故其独立使用性较强。");
        // data.put("marketabilityDivisibility", "本次估价对象为单一产权，只适宜整体使用及转让，不可分割转让。");
        // data.put("marketabilityOtherFactors", "估价对象体量及价值量较大，故其市场流动性一般，所处区域较好，交通运输较便利，基础设施条件较好，目前整体维护保养状况较好；当地房地产市场发育成熟，交易方便；于价值时点，当地同类房地产买卖交易活跃程度较好，市场景气程度较好。");
        // data.put("marketabilityConclusion", "较好");
        // data.put("quickSaleValueAnalysis", "若需对估价对象在价值时点进行短期强制处分，考虑快速变现会受到该类房地产市场需求程度、拍卖市场交易活跃程度、快速变现付款方式、处置时间较一般正常交易时间短而导致交易双方无合理谈判时间、拍卖过户等变现费用以及其他不可预见因素的影响，其成交价格将会有一定价值减损。根据估价对象具体情况，预计其可能实现的变现价值仅为其公开市场价值的50～60％左右。");
        // data.put("quickSaleTimeAnalysis", "变现时间的长短与变现能力大小正相关。根据当前处置同类房地产的变现情况，若估价对象于价值时点进行拍卖或变卖，其正常合理变现时间为12～24个月。");
        // data.put("quickSaleCostAnalysis", "估价对象若于价值时点进行拍卖，其变现过程中发生的费用、税费，一般包括拍卖公告费、拍卖佣金、诉讼律师费、增值税及附加、土地增值税、所得税、契税、印花税和交易手续费等。具体数额视估价对象具体情况，主要由拍卖公司、律师事务所、税务部门和房地产管理部门确定。");

        // ========== 比较法测算（空表占位） ==========
        data.put("comparableCaseRows", "<tr><td colspan=\"9\">无比较案例数据</td></tr>");
        data.put("comparisonConditionRows", "<tr><td colspan=\"4\">无数据</td></tr>");
        data.put("comparisonIndexRows", "<tr><td colspan=\"4\">无数据</td></tr>");
        data.put("comparisonAdjustmentRows", "<tr><td colspan=\"3\">无数据</td></tr>");
        // data.put("adjustmentCoefficientA", "");
        // data.put("adjustmentCoefficientB", "");
        // data.put("adjustmentCoefficientC", "");
        // data.put("comparisonValueA", "");
        // data.put("comparisonValueB", "");
        // data.put("comparisonValueC", "");
        data.put("weightA", "1/3");
        data.put("weightB", "1/3");
        data.put("weightC", "1/3");

        // ========== 附件图片（优先按 photo_category，描述关键词兜底） ==========
        List<SurveyPhoto> locationPhotos = firstNonEmpty(
                findPhotosByCategory(allPhotos, "LOCATION_MAP"),
                findPhotosByKeywords(allPhotos, "位置图", "位置", "地图")
        );
        List<SurveyPhoto> certificatePhotos = firstNonEmpty(
                findPhotosByCategory(allPhotos, "CERTIFICATE"),
                findPhotosByKeywords(allPhotos, "权属", "权证", "证书", "产权", "房产证", "土地证", "不动产")
        );
        List<SurveyPhoto> businessLicensePhotos = firstNonEmpty(
                findPhotosByCategory(allPhotos, "BUSINESS_LICENSE"),
                findPhotosByKeywords(allPhotos, "营业执照")
        );
        List<SurveyPhoto> recordCertificatePhotos = firstNonEmpty(
                findPhotosByCategory(allPhotos, "RECORD_CERTIFICATE"),
                findPhotosByKeywords(allPhotos, "备案证书", "备案")
        );
        List<SurveyPhoto> valuerCertificatePhotos = firstNonEmpty(
                findPhotosByCategory(allPhotos, "VALUER_CERTIFICATE"),
                findPhotosByKeywords(allPhotos, "估价师", "注册证")
        );
        List<SurveyPhoto> propertyPhotos = firstNonEmpty(
                findPhotosByCategory(allPhotos, "PROPERTY_PHOTO"),
                findPropertyPhotos(allPhotos)
        );

        data.put("locationMap", resolveFirstImagePath(locationPhotos));
        data.put("propertyPhotoHtml", buildPhotoHtml(propertyPhotos, 6));
        data.put("certificateAttachmentHtml", buildPhotoHtml(certificatePhotos, 1));
        data.put("businessLicenseImage", resolveFirstImagePath(businessLicensePhotos));
        data.put("recordCertificateImage", resolveFirstImagePath(recordCertificatePhotos));
        data.put("valuerCertificateHtml", buildPhotoHtml(valuerCertificatePhotos, 2));

        // ========== 特别说明 ==========
        // data.put("specialNotes", "");

        // ========== 确保所有模板变量都有默认值 ==========
        // String[] allVars = {
        //         "companyName", "procedureNo", "qr", "obligee",
        //         "realEstateCertificateNumber", "realEstateNumber", "rightType",
        //         "rightsNature", "use", "hillockNumber", "comMap",
        //         "caseSizeNum", "caseData", "nearAvgPriceData", "aroundSupporting1",
        //         "aroundSupporting2", "sealYf", "sealRd",
        // };
        // for (String var : allVars) {
        //     data.putIfAbsent(var, "");
        // }

        data.entrySet().removeIf(entry -> "".equals(entry.getValue()));

        return data;
    }

    /**
     * Process a FreeMarker template string with the given data model.
     */
    private String processFreeMarker(String templateContent, Map<String, Object> dataModel) throws Exception {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        Template fmTemplate = new Template("report", new StringReader(templateContent), cfg);
        try (StringWriter writer = new StringWriter()) {
            fmTemplate.process(dataModel, writer);
            return writer.toString();
        }
    }

    private boolean registerBundledFont(ITextRenderer renderer, String classpathPath) {
        try {
            ClassPathResource fontResource = new ClassPathResource(classpathPath);
            File tempFont = File.createTempFile("cjk-font-", ".ttf");
            tempFont.deleteOnExit();
            try (InputStream is = fontResource.getInputStream();
                 OutputStream os = new FileOutputStream(tempFont)) {
                is.transferTo(os);
            }
            log.info("Font size: {} bytes", tempFont.length());
            renderer.getFontResolver().addFont(tempFont.getAbsolutePath(),
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            log.info("Registered bundled CJK font: {}", classpathPath);
            return true;
        } catch (Exception e) {
            log.error("Failed to register bundled font {}: {}", classpathPath, e.getMessage(), e);
            return false;
        }
    }

    private String nvl(Object val) {
        if (val == null) return "";
        String s = val.toString();
        return s.isBlank() ? "" : s;
    }

    private String defaultIfBlank(Object val, String fallback) {
        String s = nvl(val);
        return s.isBlank() ? fallback : s;
    }

    private String firstNonBlank(Object... values) {
        if (values == null) return "";
        for (Object value : values) {
            String s = nvl(value);
            if (!s.isBlank()) return s;
        }
        return "";
    }

    private String combineUse(String landUse, String buildingUse) {
        String land = nvl(landUse);
        String building = nvl(buildingUse);
        if (!land.isBlank() && !building.isBlank()) {
            return land.equals(building) ? building : land + "/" + building;
        }
        return firstNonBlank(building, land);
    }

    private String joinNonBlank(List<String> values) {
        if (values == null || values.isEmpty()) return "";
        return values.stream()
                .map(this::nvl)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(java.util.stream.Collectors.joining("、"));
    }

    private List<String> splitNames(String raw) {
        String value = nvl(raw);
        if (value.isBlank()) return List.of();
        return Arrays.stream(value.split("[,，、/；;\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();
    }

    private List<SurveyPhoto> firstNonEmpty(List<SurveyPhoto> primary, List<SurveyPhoto> fallback) {
        return primary != null && !primary.isEmpty() ? primary : fallback;
    }

    private String fmt(Object val) {
        if (val == null) return "";
        return val.toString();
    }

    private String toChineseDate(LocalDate date) {
        if (date == null) return "";
        return toChineseYear(date.getYear()) + "年" + toChineseMonthDay(date.getMonthValue()) + "月" +
                toChineseMonthDay(date.getDayOfMonth()) + "日";
    }

    private String toChineseYear(int year) {
        String digits = "〇一二三四五六七八九";
        StringBuilder sb = new StringBuilder();
        for (char c : String.valueOf(year).toCharArray()) {
            sb.append(digits.charAt(c - '0'));
        }
        return sb.toString();
    }

    private String toChineseMonthDay(int num) {
        String[] digits = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        if (num <= 10) {
            return num == 10 ? "十" : digits[num];
        }
        if (num < 20) {
            return "十" + digits[num % 10];
        }
        if (num % 10 == 0) {
            return digits[num / 10] + "十";
        }
        return digits[num / 10] + "十" + digits[num % 10];
    }

    private String toRmbUpper(BigDecimal amount) {
        if (amount == null) return "";
        BigDecimal normalized = amount.setScale(2, java.math.RoundingMode.HALF_UP);
        long cents = normalized.movePointRight(2).longValue();
        long yuan = cents / 100;
        int jiao = (int) ((cents / 10) % 10);
        int fen = (int) (cents % 10);

        StringBuilder result = new StringBuilder(toUpperInteger(yuan)).append("元");
        if (jiao == 0 && fen == 0) {
            return result.append("整").toString();
        }
        String[] digits = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        if (jiao > 0) {
            result.append(digits[jiao]).append("角");
        } else if (fen > 0) {
            result.append("零");
        }
        if (fen > 0) {
            result.append(digits[fen]).append("分");
        }
        return result.toString();
    }

    private String toUpperInteger(long number) {
        if (number == 0) return "零";
        String[] sectionUnits = {"", "万", "亿", "兆"};
        StringBuilder result = new StringBuilder();
        int sectionIndex = 0;
        boolean needZero = false;
        while (number > 0) {
            int section = (int) (number % 10000);
            if (section == 0) {
                needZero = result.length() > 0;
            } else {
                String sectionText = sectionToUpper(section);
                if (needZero && result.length() > 0 && result.charAt(0) != '零') {
                    result.insert(0, "零");
                }
                result.insert(0, sectionText + sectionUnits[sectionIndex]);
                needZero = section < 1000;
            }
            number /= 10000;
            sectionIndex++;
        }
        return result.toString();
    }

    private String sectionToUpper(int section) {
        String[] digits = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[] units = {"", "拾", "佰", "仟"};
        StringBuilder sb = new StringBuilder();
        boolean zero = false;
        for (int i = 0; i < 4; i++) {
            int divisor = (int) Math.pow(10, 3 - i);
            int digit = section / divisor;
            section %= divisor;
            if (digit == 0) {
                zero = sb.length() > 0 && section > 0;
            } else {
                if (zero) {
                    sb.append("零");
                    zero = false;
                }
                sb.append(digits[digit]).append(units[3 - i]);
            }
        }
        return sb.toString();
    }

    private List<SurveyPhoto> findPhotosByCategory(List<SurveyPhoto> photos, String... categories) {
        if (photos == null || photos.isEmpty()) return List.of();
        Set<String> categorySet = Arrays.stream(categories)
                .filter(Objects::nonNull)
                .map(String::toUpperCase)
                .collect(java.util.stream.Collectors.toSet());
        return photos.stream()
                .filter(photo -> categorySet.contains(nvl(photo.getPhotoCategory()).toUpperCase()))
                .toList();
    }

    private List<SurveyPhoto> findPhotosByKeywords(List<SurveyPhoto> photos, String... keywords) {
        if (photos == null || photos.isEmpty()) return List.of();
        return photos.stream()
                .filter(photo -> {
                    String desc = nvl(photo.getPhotoDescription());
                    if (desc.isBlank()) return false;
                    return Arrays.stream(keywords).anyMatch(desc::contains);
                })
                .toList();
    }

    private List<SurveyPhoto> findPropertyPhotos(List<SurveyPhoto> photos) {
        if (photos == null || photos.isEmpty()) return List.of();
        List<SurveyPhoto> matched = photos.stream()
                .filter(photo -> {
                    String category = nvl(photo.getPhotoCategory());
                    if (!category.isBlank() && !"PROPERTY_PHOTO".equalsIgnoreCase(category)) {
                        return false;
                    }
                    String desc = nvl(photo.getPhotoDescription());
                    if (desc.contains("证") || desc.contains("执照") || desc.contains("备案") || desc.contains("位置") || desc.contains("地图")) {
                        return false;
                    }
                    return desc.isBlank() || desc.contains("照片") || desc.contains("外观") ||
                            desc.contains("室内") || desc.contains("环境") || desc.contains("估价对象");
                })
                .toList();
        return matched.isEmpty() ? photos : matched;
    }

    private String buildOtherRightsSummary(OwnershipInfo ownership) {
        if (ownership == null) return "";
        List<String> parts = new ArrayList<>();
        addLabeledPart(parts, "权利状态", ownership.getRightStatus());
        addLabeledPart(parts, "抵押信息", ownership.getMortgageInfo());
        addLabeledPart(parts, "查封信息", ownership.getSeizureInfo());
        addLabeledPart(parts, "租赁限制", ownership.getLeaseRestriction());
        addLabeledPart(parts, "其他权属信息", ownership.getOtherRightsInfo());
        return String.join("；", parts);
    }

    private void addLabeledPart(List<String> parts, String label, String value) {
        String text = nvl(value);
        if (!text.isBlank()) {
            parts.add(label + "：" + text);
        }
    }

    /**
     * 从照片列表中取第一张的路径，返回 HTTP URL
     */
    private String resolveFirstImagePath(List<SurveyPhoto> photos) {
        if (photos == null || photos.isEmpty()) return "";
        String photoPath = photos.get(0).getPhotoPath();
        if (photoPath == null || photoPath.isBlank()) return "";
        return toHttpUrl(photoPath);
    }

    /**
     * 将照片路径转为 HTTP URL
     */
    private String toHttpUrl(String photoPath) {
        if (photoPath.startsWith("http")) return photoPath;
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return base + "/uploads/" + photoPath;
    }

    /**
     * 将多张照片拼成 HTML
     * @param perPage 每页张数: 6=2×3网格, 2=上下两张, 1=每页一张
     */
    private String buildPhotoHtml(List<SurveyPhoto> photos, int perPage) {
        if (photos == null || photos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();

        // 统计有效照片数
        int total = (int) photos.stream().filter(p -> {
            String s = resolveFirstImagePath(List.of(p));
            return !s.isEmpty();
        }).count();
        int totalPages = (total + perPage - 1) / perPage;

        int count = 0;
        int currentPage = 1;

        for (SurveyPhoto photo : photos) {
            String src = resolveFirstImagePath(List.of(photo));
            if (src.isEmpty()) continue;
            if (count % perPage == 0) {
                if (count > 0) {
                    if (perPage == 6) sb.append("</tr></tbody></table>");
                    sb.append("</div>");
                }
                String breakStyle = currentPage < totalPages
                        ? "page-break-after:always;"
                        : "page-break-after:auto;";
                sb.append("<div style=\"").append(breakStyle).append("padding-top:20px;\">");
                if (perPage == 6) {
                    sb.append("<table style=\"width:100%;border:none;border-collapse:collapse;\"><tbody>");
                }
                currentPage++;
            }

            if (perPage == 6) {
                // 2×3 网格
                if (count % 2 == 0) sb.append("<tr>");
                sb.append("<td style=\"width:50%;padding:6px;text-align:center;vertical-align:middle;border:none;\">");
                sb.append("<img src=\"").append(src).append("\" style=\"width:310px;height:240px;object-fit:contain;\" alt=\"照片\"/>");
                sb.append("</td>");
                if (count % 2 == 1) sb.append("</tr>");
            } else if (perPage == 2) {
                // 一页两张，上下排列，各占半页高度
                sb.append("<div style=\"text-align:center;\">");
                sb.append("<img src=\"").append(src).append("\" style=\"height:400px;object-fit:contain;\" />");
                sb.append("</div>");
            } else {
                // 每页一张，居中大图
                String imgStyle = buildSingleImgStyle(photo.getPhotoPath());
                sb.append("<div style=\"text-align:center;\">");
                sb.append("<img src=\"").append(src).append("\" style=\"").append(imgStyle).append("\"/>");
                sb.append("</div>");
            }
            count++;
        }
        // 收尾
        if (perPage == 6) {
            if (count % 2 == 1) sb.append("<td style=\"border:none;\"></td></tr>");
            sb.append("</tbody></table></div>");
        } else {
            sb.append("</div>");
        }
        return sb.toString();
    }

    /**
     * 根据图片实际尺寸生成 style 属性：
     * 宽>600 或 高>800 → 限制为 600×800；否则不设尺寸
     */
    private String buildSingleImgStyle(String photoPath) {
        Path imgPath = Paths.get(uploadDir, photoPath);
        if (Files.exists(imgPath)) {
            try {
                java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(imgPath.toFile());
                if (img != null) {
                    int w = img.getWidth();
                    int h = img.getHeight();
                    if (w > 600 || h > 800) {
                        return "width:600px;height:800px;object-fit:contain;";
                    }
                }
            } catch (IOException e) {
                log.warn("Cannot read image dimensions: {}", photoPath);
            }
        }
        return "object-fit:contain;";
    }
}
