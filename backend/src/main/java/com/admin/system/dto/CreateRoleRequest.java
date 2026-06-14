package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

/**
 * 创建角色请求
 */
@Data
public class CreateRoleRequest {
    /** 角色名称 */
    @NotBlank(message = "角色名不能为空")
    private String roleName;

    /** 角色编码 */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /** 角色描述 */
    private String description;
    /** 权限ID列表 */
    private Set<Long> permissionIds;
}
