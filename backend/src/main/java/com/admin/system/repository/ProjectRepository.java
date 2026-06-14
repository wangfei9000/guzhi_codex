package com.admin.system.repository;

import com.admin.system.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @Query("SELECT p FROM Project p WHERE " +
           "(:projectCode IS NULL OR p.projectCode LIKE :projectCode) AND " +
           "(:projectName IS NULL OR p.projectName LIKE :projectName) AND " +
           "(:clientName IS NULL OR p.clientName = :clientName) AND " +
           "(:city IS NULL OR p.city LIKE :city) AND " +
           "(:address IS NULL OR p.address LIKE :address) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:onlyValuation = false OR p.valuationType IS NOT NULL) ORDER BY p.createdAt DESC")
    Page<Project> search(
            @Param("projectCode") String projectCode,
            @Param("projectName") String projectName,
            @Param("clientName") String clientName,
            @Param("city") String city,
            @Param("address") String address,
            @Param("status") String status,
            @Param("onlyValuation") boolean onlyValuation,
            Pageable pageable);

    @Query("SELECT DISTINCT p.clientName FROM Project p WHERE p.clientName IS NOT NULL AND p.clientName <> '' ORDER BY p.clientName ASC")
    List<String> findDistinctClientNames();

    List<Project> findByProjectCodeIn(List<String> projectCodes);
}
