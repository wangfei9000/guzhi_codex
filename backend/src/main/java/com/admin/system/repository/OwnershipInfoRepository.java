package com.admin.system.repository;

import com.admin.system.entity.OwnershipInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnershipInfoRepository extends JpaRepository<OwnershipInfo, Long> {
    Optional<OwnershipInfo> findByProjectId(Long projectId);
}
