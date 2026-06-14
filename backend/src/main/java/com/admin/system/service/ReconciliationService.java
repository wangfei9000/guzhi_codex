package com.admin.system.service;

import com.admin.system.dto.ReconciliationRequest;
import com.admin.system.entity.ReconciliationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReconciliationService {

    ReconciliationRecord startReconciliation(ReconciliationRequest request, Long organizationId, String organizationName);

    Page<ReconciliationRecord> listReconciliations(Long organizationId, LocalDate startTime, LocalDate endTime,
                                                   Pageable pageable);
}
