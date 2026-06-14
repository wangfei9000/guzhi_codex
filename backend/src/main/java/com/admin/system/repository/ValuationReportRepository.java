package com.admin.system.repository;

import com.admin.system.dto.ReportListDto;
import com.admin.system.entity.ValuationReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ValuationReportRepository extends JpaRepository<ValuationReport, Long> {
    List<ValuationReport> findByProjectId(Long projectId);

    boolean existsByReportCode(String reportCode);

    boolean existsByReportCodeAndIdNot(String reportCode, Long id);

    @Modifying
    @Transactional
    void deleteByProjectId(Long projectId);

    @Query("SELECT vr FROM ValuationReport vr " +
            "WHERE vr.projectId = :projectId " +
            "AND vr.reportUrl IS NOT NULL " +
            "AND vr.reportUrl <> '' " +
            "ORDER BY COALESCE(vr.updatedAt, vr.createdAt) DESC, vr.id DESC")
    List<ValuationReport> findDownloadableByProjectId(
            @Param("projectId") Long projectId,
            Pageable pageable);

    @Query(value = "SELECT new com.admin.system.dto.ReportListDto(" +
            "vr.id, vr.reportCode, p.projectCode, vr.startTime, vr.endTime, " +
            "COALESCE(vr.unitPrice, p.valuationUnitPrice), p.address, p.buildingArea, " +
            "vr.valuationResult, p.status, vr.reportUrl) " +
            "FROM ValuationReport vr " +
            "JOIN Project p ON vr.projectId = p.id " +
            "WHERE (:projectCode IS NULL OR p.projectCode LIKE :projectCode) " +
            "AND (:address IS NULL OR p.address LIKE :address) " +
            "ORDER BY vr.createdAt DESC")
    Page<ReportListDto> searchReports(
            @Param("projectCode") String projectCode,
            @Param("address") String address,
            Pageable pageable);
}
