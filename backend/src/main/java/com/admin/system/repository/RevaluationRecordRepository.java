package com.admin.system.repository;

import com.admin.system.entity.RevaluationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevaluationRecordRepository extends JpaRepository<RevaluationRecord, Long> {

    Page<RevaluationRecord> findByOrganizationId(Long organizationId, Pageable pageable);
}
