package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user")
public class SysUser extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名 */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 密码 */
    @Column(nullable = false)
    private String password;

    /** 邮箱 */
    @Column(length = 100)
    private String email;

    /** 手机号 */
    @Column(length = 20)
    private String phone;

    /** 昵称 */
    @Column(length = 50)
    private String nickname;

    /** 状态: 1-启用, 0-禁用 */
    @Column(nullable = false)
    private Integer status = 1;

    /** 所属机构 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    /** 用户角色 */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private java.util.Set<SysRole> roles = new java.util.HashSet<>();
}
