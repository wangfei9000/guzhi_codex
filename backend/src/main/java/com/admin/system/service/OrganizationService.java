package com.admin.system.service;

import com.admin.system.dto.OrganizationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrganizationService {
    Page<OrganizationDto> listOrganizations(Pageable pageable);
    List<OrganizationDto> listOptions();
    OrganizationDto getOrganizationById(Long id);
    OrganizationDto createOrganization(OrganizationDto request);
    OrganizationDto updateOrganization(Long id, OrganizationDto request);
    void deleteOrganization(Long id);
}
