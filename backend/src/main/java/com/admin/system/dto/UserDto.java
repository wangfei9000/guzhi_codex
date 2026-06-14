package com.admin.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户DTO
 */
@Data
public class UserDto {
    /** 主键ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
    /** 昵称 */
    private String nickname;
    /** 状态: 1-启用, 0-禁用 */
    private Integer status;
    /** 所属机构ID */
    private Long organizationId;
    /** 所属机构名称 */
    private String organizationName;
    /** 所属机构类型 */
    private String organizationType;
    /** 用户角色 */
    private Set<RoleDto> roles;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
