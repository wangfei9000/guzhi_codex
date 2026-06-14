package com.admin.system.repository;

import com.admin.system.entity.SurveyPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyPhotoRepository extends JpaRepository<SurveyPhoto, Long> {
    List<SurveyPhoto> findBySurveyIdIn(List<Long> surveyIds);

    List<SurveyPhoto> findBySurveyId(Long surveyId);

    List<SurveyPhoto> findByProjectIdAndPhotoDescription(Long projectId, String photoDescription);

    List<SurveyPhoto> findByProjectId(Long projectId);

    @Modifying
    @Transactional
    void deleteBySurveyIdIn(List<Long> surveyIds);
}
