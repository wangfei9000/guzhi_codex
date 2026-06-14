package com.admin.system.service.impl;

import com.admin.system.dto.RevaluationRequest;
import com.admin.system.entity.Project;
import com.admin.system.entity.RevaluationProject;
import com.admin.system.entity.RevaluationRecord;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.ProjectRepository;
import com.admin.system.repository.RevaluationProjectRepository;
import com.admin.system.repository.RevaluationRecordRepository;
import com.admin.system.service.RevaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevaluationServiceImpl implements RevaluationService {

    private final ProjectRepository projectRepository;
    private final RevaluationRecordRepository revaluationRecordRepository;
    private final RevaluationProjectRepository revaluationProjectRepository;
    private final RevaluationAsyncTask revaluationAsyncTask;

    @Override
    public RevaluationRecord startRevaluation(RevaluationRequest request, Long organizationId, String organizationName) {
        if (organizationId == null || !StringUtils.hasText(organizationName)) {
            throw new BusinessException("当前用户未设置所属机构，无法复估");
        }
        if (request == null || request.getProjectCodes() == null || request.getProjectCodes().isEmpty()) {
            throw new BusinessException("请选择需要复估的项目");
        }

        Set<String> projectCodes = request.getProjectCodes().stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (projectCodes.isEmpty()) {
            throw new BusinessException("请选择需要复估的项目");
        }

        List<Project> projects = projectRepository.findByProjectCodeIn(projectCodes.stream().toList());
        Map<String, Project> projectByCode = projects.stream()
                .filter(p -> organizationName.equals(p.getClientName()))
                .collect(Collectors.toMap(Project::getProjectCode, Function.identity(), (a, b) -> a));

        List<String> validProjectCodes = projectCodes.stream()
                .filter(projectByCode::containsKey)
                .toList();
        if (validProjectCodes.isEmpty()) {
            throw new BusinessException("未找到当前机构可复估的项目");
        }

        RevaluationRecord record = new RevaluationRecord();
        record.setOrganizationId(organizationId);
        record.setRevaluationDate(LocalDate.now());
        record.setResult("进行中");
        record.setRemark(request.getRemark());
        record = revaluationRecordRepository.save(record);

        Long revaluationId = record.getId();
        List<RevaluationProject> details = validProjectCodes.stream().map(projectCode -> {
            RevaluationProject detail = new RevaluationProject();
            detail.setRevaluationId(revaluationId);
            detail.setProjectCode(projectCode);
            detail.setRemark("进行中");
            return detail;
        }).toList();
        revaluationProjectRepository.saveAll(details);

        revaluationAsyncTask.execute(revaluationId, validProjectCodes);
        return record;
    }

    @Override
    public Page<RevaluationRecord> listRevaluations(Long organizationId, Pageable pageable) {
        if (organizationId == null) {
            return Page.empty(pageable);
        }
        return revaluationRecordRepository.findByOrganizationId(organizationId, pageable);
    }
}
