package com.admin.system.dto;

import lombok.Data;
import java.util.Set;

/**
 * 更新用户请求
 */
@Data
public class UpdateUserRequest {
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
    /** 角色ID列表 */
    private Set<Long> roleIds;
}
