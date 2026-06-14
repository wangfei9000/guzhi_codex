package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建权限请求
 */
@Data
public class CreatePermissionRequest {
    /** 权限名称 */
    @NotBlank(message = "权限名不能为空")
    private String permName;

    /** 权限编码 */
    @NotBlank(message = "权限编码不能为空")
    private String permCode;

    /** 父权限ID */
    private Long parentId;

    /** 类型: MENU-菜单, BUTTON-按钮 */
    @NotBlank(message = "类型不能为空")
    private String type;

    /** 前端路由路径 */
    private String path;
    /** 图标 */
    private String icon;
    /** 排序号 */
    private Integer sortOrder = 0;
}
