package com.admin.system;

import com.lowagie.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvReportPdfGenerationTest {

    private static final String DEFAULT_CSV_PATH = "/Users/edy/Documents/模拟数据清单50.csv";
    private static final String DEFAULT_TEMPLATE_FILE = "report-template.html";
    private static final DateTimeFormatter CN_DATE = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    //@Test
    void generateFirstPdfFromCsv() throws Exception {
        Assumptions.assumeTrue(Boolean.getBoolean("csvReport.generateSample"),
                "Run with -DcsvReport.generateSample=true to generate the first CSV report PDF");

        Path csvPath = configuredCsvPath();
        Path templatePath = configuredTemplatePath();
        Path outputDir = configuredOutputDir().resolve("sample");

        Assumptions.assumeTrue(Files.exists(csvPath), "CSV not found: " + csvPath);
        Assumptions.assumeTrue(Files.exists(templatePath), "Template not found: " + templatePath);

        List<CsvRow> rows = readCsv(csvPath);
        assertFalse(rows.isEmpty(), "CSV has no data rows");

        GeneratedReport report = generateOne(rows.get(0), templatePath, outputDir);
        System.out.println("Generated sample PDF: " + report.pdfPath().toAbsolutePath());
        System.out.println("Generated sample HTML: " + report.htmlPath().toAbsolutePath());

        assertTrue(Files.size(report.pdfPath()) > 0, "Generated PDF is empty");
    }

    //@Test
    void generateAllPdfsFromCsvWhenEnabled() throws Exception {
        Assumptions.assumeTrue(Boolean.getBoolean("csvReport.generateAll"),
                "Run with -DcsvReport.generateAll=true to generate all CSV reports");

        Path csvPath = configuredCsvPath();
        Path templatePath = configuredTemplatePath();
        Path outputDir = configuredOutputDir().resolve("all");

        Assumptions.assumeTrue(Files.exists(csvPath), "CSV not found: " + csvPath);
        Assumptions.assumeTrue(Files.exists(templatePath), "Template not found: " + templatePath);

        List<CsvRow> rows = readCsv(csvPath);
        assertFalse(rows.isEmpty(), "CSV has no data rows");

        Files.createDirectories(outputDir);
        List<String> summary = new ArrayList<>();
        summary.add("row_no,report_no,address,pdf_path,status");
        for (CsvRow row : rows) {
            try {
                GeneratedReport report = generateOne(row, templatePath, outputDir);
                summary.add(csvCell(row.sourceRowNo()) + "," +
                        csvCell(report.reportNo()) + "," +
                        csvCell(row.value("证载地址")) + "," +
                        csvCell(report.pdfPath().toAbsolutePath().toString()) + ",success");
            } catch (Exception e) {
                summary.add(csvCell(row.sourceRowNo()) + "," +
                        csvCell(reportNo(row)) + "," +
                        csvCell(row.value("证载地址")) + "," +
                        csvCell("") + "," +
                        csvCell("failed: " + e.getMessage()));
            }
        }
        Path summaryPath = outputDir.resolve("summary.csv");
        Files.write(summaryPath, summary, StandardCharsets.UTF_8);
        System.out.println("Generated all CSV reports under: " + outputDir.toAbsolutePath());
        System.out.println("Summary: " + summaryPath.toAbsolutePath());
    }

    private GeneratedReport generateOne(CsvRow row, Path templatePath, Path outputDir) throws Exception {
        Files.createDirectories(outputDir);

        String reportNo = reportNo(row);
        PriceMatch price = resolvePrice(row);
        Map<String, Object> dataModel = buildDataModel(row, reportNo, price);
        System.out.println("CSV row " + row.sourceRowNo() + " reportNo=" + reportNo +
                ", priceSource=" + price.source());
        String templateContent = Files.readString(templatePath, StandardCharsets.UTF_8);
        String html = renderTemplate(templateContent, dataModel);
        html = localizeImageSources(html, outputDir.resolve("assets"));

        String fileBaseName = outputBaseName(row, reportNo);
        Path htmlPath = outputDir.resolve(fileBaseName + ".html");
        Path pdfPath = outputDir.resolve(fileBaseName + ".pdf");
        Files.writeString(htmlPath, html, StandardCharsets.UTF_8);
        writePdf(html, pdfPath);
        return new GeneratedReport(reportNo, htmlPath, pdfPath);
    }

    private Map<String, Object> buildDataModel(CsvRow row, String reportNo, PriceMatch price) {
        Map<String, Object> data = new LinkedHashMap<>();

        LocalDate issueDate = configuredDate("csvReport.issueDate", LocalDate.now());
        LocalDate valuationTime = configuredDate("csvReport.valuationTime", LocalDate.now());
        LocalDate validEndDate = issueDate.plusYears(1).minusDays(1);
        BigDecimal buildingArea = decimal(row.value("建筑面积（㎡）"));
        BigDecimal unitPrice = price.unitPrice() != null
                ? price.unitPrice()
                : configuredDecimal("csvReport.unitPrice", new BigDecimal("30000"));
        BigDecimal totalYuan = price.totalPriceYuan() != null
                ? price.totalPriceYuan()
                : buildingArea.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWan = totalYuan.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
        LocalDate valueDate = valuationTime;

        String address = row.value("证载地址");
        String communityName = row.value("小区名称");
        String city = row.value("城市");
        String district = row.value("行政区域");
        String street = row.value("所在街道（路）");
        String assetType = defaultIfBlank(row.value("押品种类"), "普通商品用房");
        String actualUse = System.getProperty("csvReport.actualUse", "住宅");
        String projectName = defaultIfBlank(address, communityName) + "房地产线上估价项目";

        data.put("projectName", htmlText(projectName));
        data.put("reportNo", htmlText(reportNo));
        data.put("propertyAddress", htmlText(address));
        data.put("assetType", htmlText(assetType));
        data.put("buildingArea", moneyless(buildingArea));
        data.put("actualUse", htmlText(actualUse));
        data.put("registeredUse", htmlText(actualUse));
        data.put("ownerName", htmlText(System.getProperty("csvReport.ownerName", "未披露")));
        data.put("rightNature", htmlText(System.getProperty("csvReport.rightNature", "未披露")));
        data.put("floorName", htmlText(System.getProperty("csvReport.floorName", "未披露")));
        data.put("totalFloor", htmlText(System.getProperty("csvReport.totalFloor", "未披露")));
        data.put("landUseRightArea", htmlText(System.getProperty("csvReport.landUseRightArea", "未披露")));

        data.put("referenceUnitPrice", moneyless(unitPrice));
        data.put("referenceTotalPrice", moneyless(totalWan));
        data.put("referenceTotalPriceYuan", moneyless(totalYuan));
        data.put("capitalamount", htmlText(toRmbUpper(totalYuan)));

        data.put("issueDate", issueDate.format(CN_DATE));
        data.put("issueDateCn", toChineseDate(issueDate));
        data.put("valueDate", valueDate.format(CN_DATE));
        data.put("valueDateCn", toChineseDate(valueDate));
        data.put("validStartDate", issueDate.format(CN_DATE));
        data.put("validEndDate", validEndDate.format(CN_DATE));
        data.put("siteInspectionDateCn", toChineseDate(valueDate));

        putDefaultReportValues(data);
        data.put("specialNotes", htmlText("本报告依据CSV模拟数据生成，用于批量PDF版式和字段映射预览。"));
        data.put("surroundingEnvironment", htmlText(buildSurroundingText(city, district, street, communityName)));
        data.put("locationDescription", htmlText("估价对象坐落于" + address + "。"));
        data.put("marketabilityOtherFactors", htmlText("估价对象位于" + defaultIfBlank(city, "所在城市") +
                defaultIfBlank(district, "") + "，周边居住配套较为成熟，区域内同类住宅具备一定市场流动性。"));
        data.put("priceSource", htmlText(price.source()));

        return data;
    }

    private String localizeImageSources(String html, Path imageDir) throws Exception {
        Files.createDirectories(imageDir);
        Pattern pattern = Pattern.compile("(<img\\b[^>]*\\bsrc=\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        StringBuffer result = new StringBuffer();
        Map<String, Path> downloaded = new HashMap<>();

        while (matcher.find()) {
            String src = matcher.group(2);
            String replacementSrc = src;
            if (src.startsWith("http://") || src.startsWith("https://")) {
                Path localFile = downloaded.computeIfAbsent(src, key -> {
                    try {
                        return downloadImage(key, imageDir);
                    } catch (Exception e) {
                        throw new IllegalStateException("Cannot download image: " + key, e);
                    }
                });
                replacementSrc = localFile.toUri().toString();
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(1) + replacementSrc + matcher.group(3)));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private Path downloadImage(String src, Path imageDir) throws Exception {
        URI uri = URI.create(src);
        Path target = imageDir.resolve(localImageFileName(uri));
        if (Files.exists(target) && Files.size(target) > 0) {
            return target;
        }

        List<URI> candidates = new ArrayList<>();
        candidates.add(uri);
        if ("yan9000.top".equalsIgnoreCase(uri.getHost()) && uri.getPath().startsWith("/uploads/")) {
            candidates.add(URI.create("http://121.40.244.102:8080" + uri.getRawPath()));
        }

        Exception lastError = null;
        for (URI candidate : candidates) {
            try {
                download(candidate.toURL(), target);
                System.out.println("Downloaded image: " + candidate + " -> " + target.toAbsolutePath());
                return target;
            } catch (Exception e) {
                lastError = e;
                Files.deleteIfExists(target);
                System.out.println("Image download failed: " + candidate + " (" + e.getMessage() + ")");
            }
        }
        throw lastError != null ? lastError : new IllegalStateException("No image download candidate for " + src);
    }

    private void download(URL url, Path target) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8");
        int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new IllegalStateException("HTTP " + status);
        }
        try (InputStream input = connection.getInputStream()) {
            Files.copy(input, target);
        } finally {
            connection.disconnect();
        }
        if (Files.size(target) == 0) {
            throw new IllegalStateException("Downloaded empty image");
        }
    }

    private String localImageFileName(URI uri) {
        String path = uri.getPath();
        String extension = ".img";
        int dot = path.lastIndexOf('.');
        if (dot >= 0 && dot > path.lastIndexOf('/')) {
            extension = path.substring(dot);
            if (extension.length() > 8) {
                extension = ".img";
            }
        }
        String base = (uri.getHost() + "-" + path)
                .replaceAll("[^A-Za-z0-9._-]+", "_")
                .replaceAll("_+", "_");
        if (base.length() > 120) {
            base = base.substring(base.length() - 120);
        }
        if (!base.endsWith(extension)) {
            base += extension;
        }
        return base;
    }

    private PriceMatch resolvePrice(CsvRow row) throws Exception {
        if (!Boolean.parseBoolean(System.getProperty("csvReport.useDbPrice", "true"))) {
            return PriceMatch.none("disabled");
        }

        String city = row.value("城市");
        String address = row.value("证载地址");
        if (address.isBlank()) {
            return PriceMatch.none("missing address");
        }
        LocalDate valuationTime = configuredDate("csvReport.valuationTime", LocalDate.now());

        try (Connection connection = openDatasourceConnection()) {
            PriceMatch exact = queryPrice(connection,
                    "SELECT unit_price, total_price, area, valuation_time, address " +
                            "FROM valuation_price " +
                            "WHERE city = ? AND address = ? AND valuation_time = ? " +
                            "ORDER BY valuation_time DESC NULLS LAST, id DESC LIMIT 1",
                    city, address, valuationTime);
            if (exact.matched()) {
                return exact.withSource("db today exact city+address: " + exact.matchedAddress());
            }

            PriceMatch exactAddress = queryPrice(connection,
                    "SELECT unit_price, total_price, area, valuation_time, address " +
                            "FROM valuation_price " +
                            "WHERE address = ? AND valuation_time = ? " +
                            "ORDER BY valuation_time DESC NULLS LAST, id DESC LIMIT 1",
                    address, valuationTime);
            if (exactAddress.matched()) {
                return exactAddress.withSource("db today exact address: " + exactAddress.matchedAddress());
            }

            PriceMatch cityContains = queryPrice(connection,
                    "SELECT unit_price, total_price, area, valuation_time, address " +
                            "FROM valuation_price " +
                            "WHERE city = ? AND valuation_time = ? AND (address LIKE ? OR ? LIKE '%' || address || '%') " +
                            "ORDER BY valuation_time DESC NULLS LAST, id DESC LIMIT 1",
                    city, valuationTime, "%" + address + "%", address);
            if (cityContains.matched()) {
                return cityContains.withSource("db today fuzzy city+address: " + cityContains.matchedAddress());
            }

            PriceMatch addressOnly = queryPrice(connection,
                    "SELECT unit_price, total_price, area, valuation_time, address " +
                            "FROM valuation_price " +
                            "WHERE valuation_time = ? AND (address = ? OR address LIKE ? OR ? LIKE '%' || address || '%') " +
                            "ORDER BY valuation_time DESC NULLS LAST, id DESC LIMIT 1",
                    valuationTime, address, "%" + address + "%", address);
            if (addressOnly.matched()) {
                return addressOnly.withSource("db today fuzzy address: " + addressOnly.matchedAddress());
            }

            if (Boolean.parseBoolean(System.getProperty("csvReport.allowLatestPriceFallback", "true"))) {
                PriceMatch latestAddressOnly = queryPrice(connection,
                        "SELECT unit_price, total_price, area, valuation_time, address " +
                                "FROM valuation_price " +
                                "WHERE address = ? OR address LIKE ? OR ? LIKE '%' || address || '%' " +
                                "ORDER BY valuation_time DESC NULLS LAST, id DESC LIMIT 1",
                        address, "%" + address + "%", address);
                if (latestAddressOnly.matched()) {
                    return latestAddressOnly.withSource("db latest fallback address: " +
                            latestAddressOnly.matchedAddress() + ", valuation_time=" +
                            latestAddressOnly.valuationTime());
                }
            }
        }

        return PriceMatch.none("db no match");
    }

    private Connection openDatasourceConnection() throws Exception {
        String url = System.getProperty("csvReport.dbUrl", datasourceProperty("url"));
        String username = System.getProperty("csvReport.dbUsername", datasourceProperty("username"));
        String password = System.getProperty("csvReport.dbPassword", datasourceProperty("password"));
        return DriverManager.getConnection(url, username, password);
    }

    private PriceMatch queryPrice(Connection connection, String sql, Object... args) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof LocalDate localDate) {
                    statement.setDate(i + 1, Date.valueOf(localDate));
                } else {
                    statement.setString(i + 1, arg == null ? null : arg.toString());
                }
            }
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return PriceMatch.none("not matched");
                }
                BigDecimal area = rs.getBigDecimal("area");
                BigDecimal unitPrice = rs.getBigDecimal("unit_price");
                BigDecimal totalPriceWan = rs.getBigDecimal("total_price");
                BigDecimal totalPriceYuan = totalPriceWan != null
                        ? totalPriceWan.multiply(new BigDecimal("10000")).setScale(2, RoundingMode.HALF_UP)
                        : null;
                Date valuationTime = rs.getDate("valuation_time");
                return new PriceMatch(
                        unitPrice,
                        totalPriceYuan,
                        area,
                        valuationTime == null ? null : valuationTime.toLocalDate(),
                        rs.getString("address"),
                        "db",
                        true
                );
            }
        }
    }

    private String datasourceProperty(String key) throws Exception {
        Path applicationYml = resolveBackendPath("src/main/resources/application.yml",
                "backend/src/main/resources/application.yml");
        List<String> lines = Files.readAllLines(applicationYml, StandardCharsets.UTF_8);
        boolean inDatasource = false;
        int datasourceIndent = -1;
        for (String line : lines) {
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }
            int indent = leadingSpaces(line);
            String trimmed = line.trim();
            if (trimmed.equals("datasource:")) {
                inDatasource = true;
                datasourceIndent = indent;
                continue;
            }
            if (inDatasource && indent <= datasourceIndent) {
                inDatasource = false;
            }
            if (inDatasource && trimmed.startsWith(key + ":")) {
                return trimmed.substring((key + ":").length()).trim();
            }
        }
        throw new IllegalStateException("spring.datasource." + key + " not found in " + applicationYml);
    }

    private int leadingSpaces(String value) {
        int count = 0;
        while (count < value.length() && value.charAt(count) == ' ') {
            count++;
        }
        return count;
    }

    private void putDefaultReportValues(Map<String, Object> data) {
        data.putIfAbsent("valuationOrg", "浙江德衡房地产土地资产评估有限公司");
        data.putIfAbsent("orgAddress", "浙江省杭州市上城区四季青街道钱塘航空大厦2幢1510室");
        data.putIfAbsent("orgPhone", "0571-88060768");
        data.putIfAbsent("orgEmail", "124400690@qq.com");
        data.putIfAbsent("legalRepresentative", "韩松");
        data.putIfAbsent("unifiedSocialCreditCode", "91331001768682598N");
        data.putIfAbsent("organizationForm", "有限责任公司（自然人投资或控股）");
        data.putIfAbsent("firstRecordDate", "2005-03-02");
        data.putIfAbsent("recordLevel", "一级");
        data.putIfAbsent("recordCertificateNo", "浙建房估证字[2005]002号");
        data.putIfAbsent("recordCertificateValidPeriod", "2024年04月29日至2027年03月07日");
        data.putIfAbsent("contactName", "金德敏");

        data.putIfAbsent("valuer1Name", "韩松");
        data.putIfAbsent("valuer1CertNo", "6120050037");
        data.putIfAbsent("valuer2Name", "王义贵");
        data.putIfAbsent("valuer2CertNo", "3320180164");
        data.putIfAbsent("valuer1Name1", data.get("valuer1Name"));
        data.putIfAbsent("valuer1CertNo1", data.get("valuer1CertNo"));
        data.putIfAbsent("valuer2Name1", data.get("valuer2Name"));
        data.putIfAbsent("valuer2CertNo1", data.get("valuer2CertNo"));

        data.putIfAbsent("valueType", "抵押价值");
        data.putIfAbsent("valuationMethod", "比较法");
        data.putIfAbsent("parcelShape", "形状较规则，便于建筑物布置。");
        data.putIfAbsent("terrain", "地势平坦，无明显坡度。");
        data.putIfAbsent("landLevel", "该宗地与相邻土地、道路基本齐平。");
        data.putIfAbsent("soilCondition", "该宗地为城镇住宅用地，未见明显不利土壤条件。");
        data.putIfAbsent("landDevelopmentLevel", "宗地红线外基础设施达到通路、供电、供水、排水、通讯等条件。");
        data.putIfAbsent("landscape", "建筑物间距适中，景观条件一般。");
        data.putIfAbsent("facilitiesCondition", "估价对象所在区域公共配套较完善，房屋配套设施基本齐全。");
        data.putIfAbsent("spaceLayout", "住宅常规户型布局。");
        data.putIfAbsent("indoorHeight", "标准层高");
        data.putIfAbsent("orientation", "未披露");
        data.putIfAbsent("maintenanceCondition", "未见重大质量缺陷，整体维护状况按正常使用状态假定。");
        data.putIfAbsent("otherRightsStatus", "根据委托方要求，本次估价不考虑抵押、担保等他项权利影响。");

        data.putIfAbsent("incomeApproachReason", "根据评估对象利用类型及所在区域房地产市场情况，周边租赁收益资料不充分，未来收益和风险难以稳定预测，故不选用收益法。");
        data.putIfAbsent("costApproachReason", "估价对象为住宅房地产，土地取得成本、建筑物建造成本等资料较难完整取得，且区域内同类交易实例较易获取，故不宜采用成本法。");
        data.putIfAbsent("marketabilityGeneralUse", "本次估价对象为住宅房地产，通用性较好。");
        data.putIfAbsent("marketabilityIndependentUse", "估价对象可独立使用，独立使用性较强。");
        data.putIfAbsent("marketabilityDivisibility", "估价对象适宜整体使用及转让，不宜分割转让。");
        data.putIfAbsent("marketabilityConclusion", "较好");
        data.putIfAbsent("quickSaleValueAnalysis", "若短期强制处分，成交价格可能因处置周期、付款方式和交易费用等因素产生一定价值减损。");
        data.putIfAbsent("quickSaleTimeAnalysis", "根据同类房地产处置经验，正常合理变现时间一般为12至24个月。");
        data.putIfAbsent("quickSaleCostAnalysis", "处置过程中可能发生拍卖公告费、拍卖佣金、税费、交易手续费等，具体以相关部门和处置机构确认为准。");
    }

    private String renderTemplate(String templateContent, Map<String, Object> dataModel) throws Exception {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        Template template = new Template("csv-report", new StringReader(templateContent), cfg);
        try (StringWriter writer = new StringWriter()) {
            template.process(dataModel, writer);
            return writer.toString();
        }
    }

    private void writePdf(String html, Path pdfPath) throws Exception {
        ITextRenderer renderer = new ITextRenderer();
        Path fontPath = resolveBackendPath("src/main/resources/fonts/Arial Unicode.ttf",
                "backend/src/main/resources/fonts/Arial Unicode.ttf");
        renderer.getFontResolver().addFont(fontPath.toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.setDocumentFromString(html, workspaceRoot().toUri().toString());
        renderer.layout();
        try (var output = Files.newOutputStream(pdfPath)) {
            renderer.createPDF(output);
        }
    }

    private List<CsvRow> readCsv(Path csvPath) throws Exception {
        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return List.of();
        }
        List<String> headers = parseCsvLine(stripBom(lines.get(0)));
        List<CsvRow> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            List<String> cells = parseCsvLine(line);
            Map<String, String> values = new LinkedHashMap<>();
            for (int col = 0; col < headers.size(); col++) {
                values.put(headers.get(col), col < cells.size() ? cells.get(col).trim() : "");
            }
            rows.add(new CsvRow(String.valueOf(i), values));
        }
        return rows;
    }

    private List<String> parseCsvLine(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cell.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                cells.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(ch);
            }
        }
        cells.add(cell.toString());
        return cells;
    }

    private Path configuredCsvPath() {
        return Paths.get(System.getProperty("csvReport.csvPath", DEFAULT_CSV_PATH)).toAbsolutePath().normalize();
    }

    private Path configuredTemplatePath() {
        String configured = System.getProperty("csvReport.templatePath");
        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }
        Path rootTemplate = workspaceRoot().resolve(DEFAULT_TEMPLATE_FILE);
        if (Files.exists(rootTemplate)) {
            return rootTemplate;
        }
        return workspaceRoot().resolve("..").resolve(DEFAULT_TEMPLATE_FILE).toAbsolutePath().normalize();
    }

    private Path configuredOutputDir() {
        String configured = System.getProperty("csvReport.outputDir");
        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }
        return resolveBackendPath("target/csv-report-pdfs", "backend/target/csv-report-pdfs");
    }

    private Path resolveBackendPath(String backendRelative, String rootRelative) {
        Path userDir = workspaceRoot();
        Path backendPath = userDir.resolve(backendRelative);
        if (Files.exists(backendPath.getParent()) || Files.exists(backendPath)) {
            return backendPath.toAbsolutePath().normalize();
        }
        return userDir.resolve(rootRelative).toAbsolutePath().normalize();
    }

    private Path workspaceRoot() {
        return Paths.get("").toAbsolutePath().normalize();
    }

    private String reportNo(CsvRow row) {
        String prefix = System.getProperty("csvReport.reportPrefix", "CSV");
        int no = Integer.parseInt(defaultIfBlank(row.value("序号"), row.sourceRowNo()));
        return prefix + "-" + String.format("%04d", no);
    }

    private String outputBaseName(CsvRow row, String reportNo) {
        String sequenceNo = defaultIfBlank(row.value("序号"), row.sourceRowNo());
        String address = defaultIfBlank(row.value("证载地址"), "未填写地址");
        return sanitizeFileName(sequenceNo + "-" + address);
    }

    private String sanitizeFileName(String value) {
        String cleaned = value
                .replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]+", "_")
                .replaceAll("\\s+", " ")
                .trim();
        return cleaned.isEmpty() ? "report" : cleaned;
    }

    private LocalDate configuredDate(String key, LocalDate fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return LocalDate.parse(value);
    }

    private BigDecimal configuredDecimal(String key, BigDecimal fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return new BigDecimal(value.trim());
    }

    private BigDecimal decimal(String value) {
        String normalized = defaultIfBlank(value, "0").replace(",", "").trim();
        return new BigDecimal(normalized);
    }

    private String moneyless(BigDecimal value) {
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.scale() < 0 ? normalized.setScale(0).toPlainString() : normalized.toPlainString();
    }

    private String buildSurroundingText(String city, String district, String street, String communityName) {
        List<String> parts = new ArrayList<>();
        if (!city.isBlank()) parts.add(city);
        if (!district.isBlank()) parts.add(district);
        if (!street.isBlank()) parts.add(street);
        if (!communityName.isBlank()) parts.add(communityName);
        if (parts.isEmpty()) {
            return "估价对象周边居住氛围较成熟，生活配套较完善。";
        }
        return "估价对象位于" + String.join("", parts) + "，周边居住氛围较成熟，生活配套较完善。";
    }

    private String toChineseDate(LocalDate date) {
        return toChineseYear(date.getYear()) + "年" + toChineseMonthDay(date.getMonthValue()) + "月" +
                toChineseMonthDay(date.getDayOfMonth()) + "日";
    }

    private String toChineseYear(int year) {
        String digits = "〇一二三四五六七八九";
        StringBuilder result = new StringBuilder();
        for (char ch : String.valueOf(year).toCharArray()) {
            result.append(digits.charAt(ch - '0'));
        }
        return result.toString();
    }

    private String toChineseMonthDay(int value) {
        String[] digits = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        if (value <= 10) {
            return value == 10 ? "十" : digits[value];
        }
        if (value < 20) {
            return "十" + digits[value % 10];
        }
        if (value % 10 == 0) {
            return digits[value / 10] + "十";
        }
        return digits[value / 10] + "十" + digits[value % 10];
    }

    private String toRmbUpper(BigDecimal amount) {
        BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
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
        if (number == 0) {
            return "零";
        }
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
        StringBuilder result = new StringBuilder();
        boolean zero = false;
        for (int i = 0; i < 4; i++) {
            int divisor = (int) Math.pow(10, 3 - i);
            int digit = section / divisor;
            section %= divisor;
            if (digit == 0) {
                zero = result.length() > 0 && section > 0;
            } else {
                if (zero) {
                    result.append("零");
                    zero = false;
                }
                result.append(digits[digit]).append(units[3 - i]);
            }
        }
        return result.toString();
    }

    private String stripBom(String value) {
        if (!value.isEmpty() && value.charAt(0) == '\uFEFF') {
            return value.substring(1);
        }
        return value;
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String htmlText(String value) {
        return defaultIfBlank(value, "")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String csvCell(String value) {
        String text = defaultIfBlank(value, "");
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    private record CsvRow(String sourceRowNo, Map<String, String> values) {
        String value(String header) {
            return values.getOrDefault(header, "").trim();
        }
    }

    private record GeneratedReport(String reportNo, Path htmlPath, Path pdfPath) {
    }

    private record PriceMatch(BigDecimal unitPrice, BigDecimal totalPriceYuan, BigDecimal area,
                              LocalDate valuationTime, String matchedAddress, String source, boolean matched) {
        static PriceMatch none(String source) {
            return new PriceMatch(null, null, null, null, "", source, false);
        }

        PriceMatch withSource(String source) {
            return new PriceMatch(unitPrice, totalPriceYuan, area, valuationTime, matchedAddress, source, matched);
        }
    }
}
