package com.admin.system.service.impl;

import com.admin.system.entity.Project;
import com.admin.system.entity.RevaluationProject;
import com.admin.system.entity.RevaluationRecord;
import com.admin.system.entity.ValuationPrice;
import com.admin.system.repository.ProjectRepository;
import com.admin.system.repository.RevaluationProjectRepository;
import com.admin.system.repository.RevaluationRecordRepository;
import com.admin.system.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevaluationAsyncTask {

    private final ProjectRepository projectRepository;
    private final RevaluationRecordRepository revaluationRecordRepository;
    private final RevaluationProjectRepository revaluationProjectRepository;
    private final ProjectService projectService;

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    @Async
    public void execute(Long revaluationId, List<String> projectCodes) {
        try {
            List<Project> projects = projectRepository.findByProjectCodeIn(projectCodes);
            Map<String, Project> projectByCode = projects.stream()
                    .collect(Collectors.toMap(Project::getProjectCode, Function.identity(), (a, b) -> a));
            List<RevaluationProject> details = revaluationProjectRepository.findByRevaluationId(revaluationId);

            for (RevaluationProject detail : details) {
                Project project = projectByCode.get(detail.getProjectCode());
                if (project == null) {
                    detail.setRemark("项目不存在");
                    revaluationProjectRepository.save(detail);
                    continue;
                }

                Optional<ValuationPrice> price = projectService.findValuationPrice(
                        project.getCity(), project.getAddress(), project.getValuationTime());
                if (price.isPresent()) {
                    ValuationPrice valuationPrice = price.get();
                    detail.setUnitPrice(valuationPrice.getUnitPrice());
                    detail.setTotalPrice(valuationPrice.getTotalPrice());
                    detail.setRemark("复估成功");
                } else {
                    detail.setUnitPrice(null);
                    detail.setTotalPrice(null);
                    detail.setRemark("该小区无法估值");
                }
                revaluationProjectRepository.save(detail);
            }

            String fileUrl = writeCsv(revaluationId, details, projectByCode);
            RevaluationRecord record = revaluationRecordRepository.findById(revaluationId)
                    .orElseThrow();
            record.setFileUrl(fileUrl);
            record.setResult("已完成");
            revaluationRecordRepository.save(record);
        } catch (Exception e) {
            log.error("Revaluation task failed, id={}", revaluationId, e);
            revaluationRecordRepository.findById(revaluationId).ifPresent(record -> {
                record.setResult("已完成");
                record.setRemark(appendRemark(record.getRemark(), "复估失败：" + e.getMessage()));
                revaluationRecordRepository.save(record);
            });
        }
    }

    private String writeCsv(Long revaluationId, List<RevaluationProject> details, Map<String, Project> projectByCode)
            throws IOException {
        String subDir = "revaluation/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Path dir = Paths.get(uploadDir, subDir);
        Files.createDirectories(dir);

        String fileName = "revaluation-" + revaluationId + "-" + UUID.randomUUID().toString().replace("-", "") + ".csv";
        Path file = dir.resolve(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write('\ufeff');
            writer.write("id,城市,行政区,地址,单价,总价,价值时点,小区名称");
            writer.newLine();
            for (RevaluationProject detail : details) {
                Project project = projectByCode.get(detail.getProjectCode());
                writer.write(csv(project != null ? project.getId() : null));
                writer.write(',');
                writer.write(csv(project != null ? project.getCity() : null));
                writer.write(',');
                writer.write(csv(project != null ? project.getDistrict() : null));
                writer.write(',');
                writer.write(csv(project != null ? project.getAddress() : null));
                writer.write(',');
                writer.write(csv(detail.getUnitPrice()));
                writer.write(',');
                writer.write(csv(detail.getTotalPrice()));
                writer.write(',');
                writer.write(csv(project != null ? project.getValuationTime() : null));
                writer.write(',');
                writer.write(csv(""));
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
