package com.admin.system.repository;

import com.admin.system.entity.Collateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CollateralRepository extends JpaRepository<Collateral, Long> {
    List<Collateral> findByProjectId(Long projectId);

    @Modifying
    @Transactional
    void deleteByProjectId(Long projectId);
}
