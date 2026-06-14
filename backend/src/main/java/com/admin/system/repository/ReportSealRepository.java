package com.admin.system.repository;

import com.admin.system.dto.SealListDto;
import com.admin.system.entity.ReportSeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportSealRepository extends JpaRepository<ReportSeal, Long> {

    List<ReportSeal> findByReportId(Long reportId);

    @Query(value = "SELECT new com.admin.system.dto.SealListDto(" +
            "rs.id, rs.reportId, vr.reportCode, p.projectCode, " +
            "rs.sealedReportUrl, rs.sealer, rs.sealDate, p.status) " +
            "FROM ReportSeal rs " +
            "JOIN ValuationReport vr ON rs.reportId = vr.id " +
            "JOIN Project p ON rs.projectId = p.id " +
            "WHERE (:reportCode IS NULL OR vr.reportCode LIKE :reportCode) " +
            "AND (:projectCode IS NULL OR p.projectCode LIKE :projectCode) " +
            "ORDER BY rs.createdAt DESC")
    Page<SealListDto> searchSeals(
            @Param("reportCode") String reportCode,
            @Param("projectCode") String projectCode,
            Pageable pageable);
}
