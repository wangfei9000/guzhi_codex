package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_permission")
public class SysPermission extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 权限名称 */
    @Column(nullable = false, length = 50)
    private String permName;

    /** 权限编码 */
    @Column(nullable = false, unique = true, length = 100)
    private String permCode;

    /** 父权限ID */
    private Long parentId;

    /** 类型: MENU-菜单, BUTTON-按钮 */
    @Column(nullable = false, length = 20)
    private String type;

    /** 前端路由路径 */
    @Column(length = 200)
    private String path;

    /** 图标 */
    @Column(length = 50)
    private String icon;

    /** 排序号 */
    @Column(nullable = false)
    private Integer sortOrder = 0;
}
