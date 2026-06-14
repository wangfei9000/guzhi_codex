package com.admin.system.repository;

import com.admin.system.entity.SysSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SysScheduleRepository extends JpaRepository<SysSchedule, Long> {

    @Query("SELECT s FROM SysSchedule s WHERE " +
           ":keyword IS NULL OR s.reportNo LIKE :keyword " +
           "OR s.projectAddress LIKE :keyword")
    org.springframework.data.domain.Page<SysSchedule> search(
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);
}
