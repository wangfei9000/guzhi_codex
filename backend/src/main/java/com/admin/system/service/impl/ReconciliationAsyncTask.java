package com.admin.system.service.impl;

import com.admin.system.entity.Project;
import com.admin.system.entity.ReconciliationRecord;
import com.admin.system.repository.ProjectRepository;
import com.admin.system.repository.ReconciliationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReconciliationAsyncTask {

    private final ProjectRepository projectRepository;
    private final ReconciliationRecordRepository reconciliationRecordRepository;

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    @Async
    public void execute(Long reconciliationId, String organizationName, LocalDate startTime, LocalDate endTime) {
        try {
            LocalDateTime startDateTime = startTime.atStartOfDay();
            LocalDateTime endExclusive = endTime.plusDays(1).atStartOfDay();
            Specification<Project> spec = (root, query, cb) -> cb.and(
                    cb.equal(root.get("clientName"), organizationName),
                    cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime),
                    cb.lessThan(root.get("createdAt"), endExclusive)
            );
            List<Project> projects = projectRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "createdAt"));

            String fileUrl = writeCsv(reconciliationId, projects);
            ReconciliationRecord record = reconciliationRecordRepository.findById(reconciliationId)
                    .orElseThrow();
            record.setFileUrl(fileUrl);
            record.setResult("已完成");
            reconciliationRecordRepository.save(record);
        } catch (Exception e) {
            log.error("Reconciliation task failed, id={}", reconciliationId, e);
            reconciliationRecordRepository.findById(reconciliationId).ifPresent(record -> {
                record.setResult("已完成");
                record.setRemark(appendRemark(record.getRemark(), "对账失败：" + e.getMessage()));
                reconciliationRecordRepository.save(record);
            });
        }
    }

    private String writeCsv(Long reconciliationId, List<Project> projects) throws IOException {
        String subDir = "reconciliation/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Path dir = Paths.get(uploadDir, subDir);
        Files.createDirectories(dir);

        String fileName = "reconciliation-" + reconciliationId + "-"
                + UUID.randomUUID().toString().replace("-", "") + ".csv";
        Path file = dir.resolve(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write('\ufeff');
            writer.write("项目编号,城市,行政区,地址,单价,总价,价值时点,创建日期");
            writer.newLine();
            for (Project project : projects) {
                writer.write(csv(project.getProjectCode()));
                writer.write(',');
                writer.write(csv(project.getCity()));
                writer.write(',');
                writer.write(csv(project.getDistrict()));
                writer.write(',');
                writer.write(csv(project.getAddress()));
                writer.write(',');
                writer.write(csv(project.getValuationUnitPrice()));
                writer.write(',');
                writer.write(csv(project.getValuationTotalPrice()));
                writer.write(',');
                writer.write(csv(project.getValuationTime()));
                writer.write(',');
                writer.write(csv(project.getCreatedAt()));
                writer.newLine();
            }
        }

        return subDir + "/" + fileName;
    }

    private String csv(Object value) {
        if (value == null) return "";
        String text = value instanceof BigDecimal ? ((BigDecimal) value).toPlainString() : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private String appendRemark(String oldRemark, String newRemark) {
        if (oldRemark == null || oldRemark.isBlank()) return newRemark;
        return oldRemark + "\n" + newRemark;
    }
}
