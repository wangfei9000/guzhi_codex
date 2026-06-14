package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

/**
 * 创建用户请求
 */
@Data
public class CreateUserRequest {
    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50)
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100)
    private String password;

    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
    /** 昵称 */
    private String nickname;
    /** 状态: 1-启用, 0-禁用 */
    private Integer status = 1;
    /** 所属机构ID */
    private Long organizationId;
    /** 角色ID列表 */
    private Set<Long> roleIds;
}
