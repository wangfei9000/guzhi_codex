package com.admin.system.service;

import com.admin.system.dto.ProjectDetailDto;
import com.admin.system.dto.ProjectDto;
import com.admin.system.entity.ValuationPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectService {

    Page<ProjectDto> listProjects(String projectCode, String projectName, String clientName, String city,
                                  String address, String status, Boolean onlyValuation, Pageable pageable);

    Page<ProjectDto> listBankValuations(String clientName, String city, String district,
                                        String valuationType, String status, LocalDate valuationTime,
                                        Pageable pageable);

    List<String> listClientNames();

    List<String> listValuationCities();

    List<String> listValuationDistricts(String city);

    ProjectDto getProjectById(Long id);

    ProjectDetailDto getProjectDetail(Long id);

    void saveProjectDetail(Long id, ProjectDetailDto detail);

    ProjectDto updateProject(Long id, ProjectDto dto);

    ProjectDto createProject(ProjectDto dto, String surveyor);

    ProjectDto saveAutoValuation(ProjectDto dto);

    Optional<ValuationPrice> findValuationPrice(String city, String address, LocalDate valuationTime);

    /**
     * 查询估值价格 —— 根据地址和估价时点查询估值价格
     * 返回 Map: { "valuationUnitPrice": BigDecimal, "valuationTotalPrice": BigDecimal }
     */
    java.util.Map<String, Object> queryValuationPrice(String city, String address, String valuationTime);
}
