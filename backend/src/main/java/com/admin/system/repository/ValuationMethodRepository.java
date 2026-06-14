package com.admin.system.repository;

import com.admin.system.entity.ValuationMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ValuationMethodRepository extends JpaRepository<ValuationMethod, Long> {
    List<ValuationMethod> findByReportIdIn(List<Long> reportIds);

    List<ValuationMethod> findByReportId(Long reportId);

    @Modifying
    @Transactional
    void deleteByReportIdIn(List<Long> reportIds);
}
