package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.CreateUserRequest;
import com.admin.system.dto.UpdateUserRequest;
import com.admin.system.dto.UserDto;
import com.admin.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<UserDto>> listUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<UserDto> page = userService.listUsers(keyword, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/options")
    public ApiResponse<List<Map<String, Object>>> userOptions() {
        List<Map<String, Object>> options = userService.getUserOptions();
        return ApiResponse.success(options);
    }

    @GetMapping("/by-role")
    public ApiResponse<List<Map<String, Object>>> usersByRole(@RequestParam String roleCode) {
        List<Map<String, Object>> users = userService.getUsersByRole(roleCode);
        return ApiResponse.success(users);
    }

    @GetMapping("/me")
    public ApiResponse<UserDto> currentUser(Principal principal) {
        UserDto user = userService.getCurrentUser(principal.getName());
        return ApiResponse.success(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ApiResponse.success(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);
        return ApiResponse.success("创建成功", user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDto> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        UserDto user = userService.updateUser(id, request);
        return ApiResponse.success("更新成功", user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("删除成功", null);
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.resetPassword(id, body.get("password"));
        return ApiResponse.success("密码重置成功", null);
    }
}
