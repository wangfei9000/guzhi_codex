package com.admin.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWT Token响应
 */
@Data
@AllArgsConstructor
public class TokenResponse {
    /** 访问令牌 */
    private String accessToken;
    /** 令牌类型 */
    private String tokenType;
    /** 过期时间(秒) */
    private Long expiresIn;

    public static TokenResponse of(String accessToken, Long expiresIn) {
        return new TokenResponse(accessToken, "Bearer", expiresIn);
    }
}
