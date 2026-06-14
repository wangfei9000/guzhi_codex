package com.admin.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求
 */
@Data
public class RegisterRequest {
    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度需要3-50个字符")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度需要6-100个字符")
    private String password;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 手机号 */
    private String phone;
    /** 昵称 */
    private String nickname;
}
