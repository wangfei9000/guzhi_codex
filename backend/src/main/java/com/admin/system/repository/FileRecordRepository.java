package com.admin.system.repository;

import com.admin.system.entity.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
}
