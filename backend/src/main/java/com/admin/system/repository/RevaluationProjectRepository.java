package com.admin.system.repository;

import com.admin.system.entity.RevaluationProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevaluationProjectRepository extends JpaRepository<RevaluationProject, Long> {

    List<RevaluationProject> findByRevaluationId(Long revaluationId);
}
