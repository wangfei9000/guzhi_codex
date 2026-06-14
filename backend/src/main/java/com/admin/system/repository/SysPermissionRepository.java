package com.admin.system.repository;

import com.admin.system.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {
    List<SysPermission> findByType(String type);
    List<SysPermission> findByParentIdOrderBySortOrder(Long parentId);
}
