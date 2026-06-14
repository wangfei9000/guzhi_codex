package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.ProjectDetailDto;
import com.admin.system.dto.ProjectDto;
import com.admin.system.dto.UserDto;
import com.admin.system.exception.BusinessException;
import com.admin.system.service.ProjectService;
import com.admin.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponse<ProjectDto>> listProjects(
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean onlyValuation,
            @PageableDefault(size = 10, sort = "registrationDate") Pageable pageable) {
        Page<ProjectDto> page = projectService.listProjects(
                projectCode, projectName, clientName, city, address, status, onlyValuation, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/bank-valuations")
    public ApiResponse<PageResponse<ProjectDto>> listBankValuations(
            Principal principal,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String valuationType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate valuationTime,
            @PageableDefault(size = 10, sort = "registrationDate") Pageable pageable) {
        UserDto currentUser = userService.getCurrentUser(principal.getName());
        String organizationName = currentUser.getOrganizationName();
        if (organizationName == null || organizationName.isBlank()) {
            throw new BusinessException("当前用户未设置所属机构，无法查看银行估值列表");
        }

        Page<ProjectDto> page = projectService.listBankValuations(
                organizationName, city, district, valuationType, status, valuationTime, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/client-names")
    public ApiResponse<List<String>> listClientNames() {
        return ApiResponse.success(projectService.listClientNames());
    }

    @GetMapping("/valuation-cities")
    public ApiResponse<List<String>> listValuationCities() {
        return ApiResponse.success(projectService.listValuationCities());
    }

    @GetMapping("/valuation-districts")
    public ApiResponse<List<String>> listValuationDistricts(@RequestParam String city) {
        return ApiResponse.success(projectService.listValuationDistricts(city));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectDto> getProjectById(@PathVariable Long id) {
        ProjectDto project = projectService.getProjectById(id);
        return ApiResponse.success(project);
    }

    @GetMapping("/{id}/detail")
    public ApiResponse<ProjectDetailDto> getProjectDetail(@PathVariable Long id) {
        ProjectDetailDto detail = projectService.getProjectDetail(id);
        return ApiResponse.success(detail);
    }

    @PutMapping("/{id}/detail")
    public ApiResponse<Void> saveProjectDetail(@PathVariable Long id,
                                                @RequestBody ProjectDetailDto detail) {
        projectService.saveProjectDetail(id, detail);
        return ApiResponse.success("保存成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProjectDto> updateProject(@PathVariable Long id,
                                                  @RequestBody ProjectDto dto) {
        return ApiResponse.success("更新成功", projectService.updateProject(id, dto));
    }

    @PostMapping
    public ApiResponse<ProjectDto> createProject(@RequestBody Map<String, Object> body) {
        ProjectDto dto = new ProjectDto();
        dto.setProjectName((String) body.get("projectName"));
        dto.setCity((String) body.get("city"));
        dto.setDistrict((String) body.get("district"));
        dto.setArea((String) body.get("area"));
        dto.setAddress((String) body.get("address"));
        dto.setRegistrar((String) body.get("registrar"));
        dto.setClientName((String) body.get("clientName"));
        dto.setClientContact((String) body.get("clientContact"));
        dto.setClientPhone((String) body.get("clientPhone"));
        dto.setMortgagorName((String) body.get("mortgagorName"));
        dto.setMortgagorIdCard((String) body.get("mortgagorIdCard"));
        dto.setMortgagorPhone((String) body.get("mortgagorPhone"));
        dto.setBorrowerName((String) body.get("borrowerName"));
        dto.setBorrowerIdCard((String) body.get("borrowerIdCard"));
        dto.setValuationPurpose((String) body.get("valuationPurpose"));
        dto.setValuationType((String) body.get("valuationType"));
        dto.setRemark((String) body.get("remark"));
        dto.setStatus((String) body.get("status"));
        // Handle date fields
        try {
            if (body.get("registrationDate") != null) {
                dto.setRegistrationDate(java.time.LocalDate.parse(body.get("registrationDate").toString().substring(0, 10)));
            }
            if (body.get("valuationTime") != null) {
                dto.setValuationTime(java.time.LocalDate.parse(body.get("valuationTime").toString().substring(0, 10)));
            }
        } catch (Exception ignored) {}
        // Handle expectedPrice
        try {
            if (body.get("expectedPrice") != null) {
                dto.setExpectedPrice(new java.math.BigDecimal(body.get("expectedPrice").toString()));
            }
            if (body.get("buildingArea") != null) {
                dto.setBuildingArea(new java.math.BigDecimal(body.get("buildingArea").toString()));
            }
            if (body.get("valuationUnitPrice") != null) {
                dto.setValuationUnitPrice(new java.math.BigDecimal(body.get("valuationUnitPrice").toString()));
            }
            if (body.get("valuationTotalPrice") != null) {
                dto.setValuationTotalPrice(new java.math.BigDecimal(body.get("valuationTotalPrice").toString()));
            }
        } catch (Exception ignored) {}

        String surveyor = (String) body.get("surveyor");
        return ApiResponse.success("创建成功", projectService.createProject(dto, surveyor));
    }

    @PostMapping("/auto-valuation")
    public ApiResponse<ProjectDto> autoValuation(@RequestBody ProjectDto dto) {
        return ApiResponse.success("估值完成", projectService.saveAutoValuation(dto));
    }

    /**
     * 查询估值价格 —— 根据地址和估价时点查询估值价格
     */
    @PostMapping("/valuation-price")
    public ApiResponse<Map<String, Object>> queryValuationPrice(@RequestParam(required = false) String city,
                                                                 @RequestParam String address,
                                                                 @RequestParam(required = false) String valuationTime) {
        return ApiResponse.success("查询成功", projectService.queryValuationPrice(city, address, valuationTime));
    }
}
