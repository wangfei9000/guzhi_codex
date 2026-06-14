package com.admin.system.repository;

import com.admin.system.entity.ReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliationRecordRepository extends JpaRepository<ReconciliationRecord, Long>,
        JpaSpecificationExecutor<ReconciliationRecord> {
}
