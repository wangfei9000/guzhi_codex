package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.dto.LoginRequest;
import com.admin.system.dto.RegisterRequest;
import com.admin.system.dto.TokenResponse;
import com.admin.system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ApiResponse.success(token);
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success("注册成功", null);
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        TokenResponse token = authService.refreshToken(refreshToken);
        return ApiResponse.success(token);
    }
}
