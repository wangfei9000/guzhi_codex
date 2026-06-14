package com.admin.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限DTO
 */
@Data
public class PermissionDto {
    /** 主键ID */
    private Long id;
    /** 权限名称 */
    private String permName;
    /** 权限编码 */
    private String permCode;
    /** 父权限ID */
    private Long parentId;
    /** 类型: MENU-菜单, BUTTON-按钮 */
    private String type;
    /** 前端路由路径 */
    private String path;
    /** 图标 */
    private String icon;
    /** 排序号 */
    private Integer sortOrder;
    /** 子权限列表 */
    private List<PermissionDto> children;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
