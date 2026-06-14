package com.admin.system.repository;

import com.admin.system.entity.SysNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysNotificationRepository extends JpaRepository<SysNotification, Long> {
    Page<SysNotification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    long countByUserIdAndIsReadFalse(Long userId);
}
