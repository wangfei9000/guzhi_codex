package com.admin.system.repository;

import com.admin.system.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    java.util.Optional<Organization> findFirstByOrganizationName(String organizationName);
}
