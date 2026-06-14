package com.admin.system.service;

import com.admin.system.dto.LoginRequest;
import com.admin.system.dto.RegisterRequest;
import com.admin.system.dto.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    void register(RegisterRequest request);
    TokenResponse refreshToken(String refreshToken);
}
