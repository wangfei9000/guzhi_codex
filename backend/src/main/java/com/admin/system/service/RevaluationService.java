package com.admin.system.service;

import com.admin.system.dto.RevaluationRequest;
import com.admin.system.entity.RevaluationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RevaluationService {

    RevaluationRecord startRevaluation(RevaluationRequest request, Long organizationId, String organizationName);

    Page<RevaluationRecord> listRevaluations(Long organizationId, Pageable pageable);
}
