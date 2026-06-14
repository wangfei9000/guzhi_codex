package com.admin.system.repository;

import com.admin.system.dto.SurveyListDto;
import com.admin.system.entity.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByProjectId(Long projectId);

    Optional<Survey> findBySurveyCode(String surveyCode);

    Optional<Survey> findByCode(String code);

    @Modifying
    @Transactional
    void deleteByProjectId(Long projectId);

    @Query(value = "SELECT new com.admin.system.dto.SurveyListDto(" +
            "s.id, s.surveyCode, s.code, s.surveyStatus, p.projectCode, s.surveyor, s.receptionist, " +
            "s.receptionistPhone, s.surveyDate, s.startTime, s.endTime, " +
            "s.propertyCertVerified, s.ownershipDispute, s.remark, p.address, s.projectId) " +
            "FROM Survey s " +
            "JOIN Project p ON s.projectId = p.id " +
            "WHERE (:projectCode IS NULL OR p.projectCode LIKE :projectCode) " +
            "ORDER BY s.createdAt DESC")
    Page<SurveyListDto> searchSurveys(
            @Param("projectCode") String projectCode,
            Pageable pageable);
}
