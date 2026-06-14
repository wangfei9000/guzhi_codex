package com.admin.system.service.impl;

import com.admin.system.dto.OrganizationDto;
import com.admin.system.entity.Organization;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.OrganizationRepository;
import com.admin.system.repository.ReportTemplateRepository;
import com.admin.system.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ReportTemplateRepository reportTemplateRepository;

    @Override
    public Page<OrganizationDto> listOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public List<OrganizationDto> listOptions() {
        return organizationRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public OrganizationDto getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("机构不存在"));
        return toDto(organization);
    }

    @Override
    @Transactional
    public OrganizationDto createOrganization(OrganizationDto request) {
        Organization organization = new Organization();
        applyRequest(organization, request);
        organizationRepository.save(organization);
        return toDto(organization);
    }

    @Override
    @Transactional
    public OrganizationDto updateOrganization(Long id, OrganizationDto request) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("机构不存在"));
        applyRequest(organization, request);
        organizationRepository.save(organization);
        return toDto(organization);
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new BusinessException("机构不存在");
        }
        organizationRepository.deleteById(id);
    }

    private void applyRequest(Organization organization, OrganizationDto request) {
        organization.setOrganizationType(request.getOrganizationType());
        organization.setOrganizationName(request.getOrganizationName());
        organization.setContactName(request.getContactName());
        organization.setContactPhone(request.getContactPhone());
        if (request.getReportTemplateId() != null && !reportTemplateRepository.existsById(request.getReportTemplateId())) {
            throw new BusinessException("报告模版不存在");
        }
        organization.setReportTemplateId(request.getReportTemplateId());
    }

    private OrganizationDto toDto(Organization organization) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(organization.getId());
        dto.setOrganizationType(organization.getOrganizationType());
        dto.setOrganizationName(organization.getOrganizationName());
        dto.setContactName(organization.getContactName());
        dto.setContactPhone(organization.getContactPhone());
        dto.setReportTemplateId(organization.getReportTemplateId());
        if (organization.getReportTemplateId() != null) {
            reportTemplateRepository.findById(organization.getReportTemplateId())
                    .ifPresent(template -> dto.setReportTemplateName(template.getTemplateName()));
        }
        dto.setCreatedAt(organization.getCreatedAt());
        return dto;
    }
}
