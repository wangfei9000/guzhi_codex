package com.admin.system.service.impl;

import com.admin.system.dto.LoginRequest;
import com.admin.system.dto.RegisterRequest;
import com.admin.system.dto.TokenResponse;
import com.admin.system.entity.SysRole;
import com.admin.system.entity.SysUser;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SysRoleRepository;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.security.JwtTokenProvider;
import com.admin.system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = jwtTokenProvider.generateAccessToken(request.getUsername());
        return TokenResponse.of(accessToken, 86400000L);
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());

        SysRole userRole = roleRepository.findByRoleCode("ROLE_USER")
                .orElseThrow(() -> new BusinessException("默认角色不存在"));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(401, "Token已过期，请重新登录");
        }
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        return TokenResponse.of(newAccessToken, 86400000L);
    }
}
