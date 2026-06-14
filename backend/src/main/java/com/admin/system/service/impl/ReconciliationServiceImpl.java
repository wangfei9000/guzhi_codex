package com.admin.system.service.impl;

import com.admin.system.dto.ReconciliationRequest;
import com.admin.system.entity.ReconciliationRecord;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.ReconciliationRecordRepository;
import com.admin.system.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReconciliationServiceImpl implements ReconciliationService {

    private final ReconciliationRecordRepository reconciliationRecordRepository;
    private final ReconciliationAsyncTask reconciliationAsyncTask;

    @Override
    public ReconciliationRecord startReconciliation(ReconciliationRequest request, Long organizationId,
                                                    String organizationName) {
        if (organizationId == null || !StringUtils.hasText(organizationName)) {
            throw new BusinessException("当前用户未设置所属机构，无法对账");
        }
        if (request == null || request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException("请选择开始时间和结束时间");
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }

        ReconciliationRecord record = new ReconciliationRecord();
        record.setOrganizationId(organizationId);
        record.setStartTime(request.getStartTime());
        record.setEndTime(request.getEndTime());
        record.setReconciliationDate(LocalDate.now());
        record.setResult("进行中");
        record.setRemark(request.getRemark());
        record = reconciliationRecordRepository.save(record);

        reconciliationAsyncTask.execute(record.getId(), organizationName, request.getStartTime(), request.getEndTime());
        return record;
    }

    @Override
    public Page<ReconciliationRecord> listReconciliations(Long organizationId, LocalDate startTime, LocalDate endTime,
                                                          Pageable pageable) {
        if (organizationId == null) {
            return Page.empty(pageable);
        }

        Specification<ReconciliationRecord> spec = (root, query, cb) ->
                cb.equal(root.get("organizationId"), organizationId);
        if (startTime != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startTime"), startTime));
        }
        if (endTime != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("endTime"), endTime));
        }

        return reconciliationRecordRepository.findAll(spec, pageable);
    }
}
