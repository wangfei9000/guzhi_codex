package com.admin.system.service;

import com.admin.system.dto.CreateUserRequest;
import com.admin.system.dto.UpdateUserRequest;
import com.admin.system.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<Map<String, Object>> getUserOptions();
    List<Map<String, Object>> getUsersByRole(String roleCode);
    Page<UserDto> listUsers(String keyword, Pageable pageable);
    UserDto getUserById(Long id);
    UserDto getCurrentUser(String username);
    UserDto createUser(CreateUserRequest request);
    UserDto updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    void resetPassword(Long id, String newPassword);
}
