package com.admin.system.repository;

import com.admin.system.entity.ReportReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReportReviewRepository extends JpaRepository<ReportReview, Long> {
    List<ReportReview> findByReportIdIn(List<Long> reportIds);

    List<ReportReview> findByReportId(Long reportId);

    @Modifying
    @Transactional
    void deleteByReportIdIn(List<Long> reportIds);
}
