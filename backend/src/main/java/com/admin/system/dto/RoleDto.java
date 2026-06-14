package com.admin.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 角色DTO
 */
@Data
public class RoleDto {
    /** 主键ID */
    private Long id;
    /** 角色名称 */
    private String roleName;
    /** 角色编码 */
    private String roleCode;
    /** 角色描述 */
    private String description;
    /** 角色拥有的权限 */
    private Set<PermissionDto> permissions;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
