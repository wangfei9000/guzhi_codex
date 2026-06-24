package com.admin.system.repository;

import com.admin.system.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    Optional<SysUser> findByUsername(String username);
    boolean existsByUsername(String username);
    List<SysUser> findByStatusOrderByUsernameAsc(Integer status);

    @Query("SELECT DISTINCT u FROM SysUser u " +
           "JOIN u.roles r " +
           "WHERE r.roleCode = :roleCode OR r.roleName LIKE %:roleName%")
    List<SysUser> findByRole(@Param("roleCode") String roleCode, @Param("roleName") String roleName);
}
