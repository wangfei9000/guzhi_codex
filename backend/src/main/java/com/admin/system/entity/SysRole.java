package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role")
public class SysRole extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 角色名称 */
    @Column(nullable = false, unique = true, length = 50)
    private String roleName;

    /** 角色编码 */
    @Column(nullable = false, unique = true, length = 50)
    private String roleCode;

    /** 角色描述 */
    @Column(length = 200)
    private String description;

    /** 角色拥有的权限 */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private java.util.Set<SysPermission> permissions = new java.util.HashSet<>();
}
