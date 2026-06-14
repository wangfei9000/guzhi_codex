package com.admin.system.service.impl;

import com.admin.system.dto.CreateUserRequest;
import com.admin.system.dto.RoleDto;
import com.admin.system.dto.PermissionDto;
import com.admin.system.dto.UpdateUserRequest;
import com.admin.system.dto.UserDto;
import com.admin.system.entity.Organization;
import com.admin.system.entity.SysRole;
import com.admin.system.entity.SysUser;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.OrganizationRepository;
import com.admin.system.repository.SysRoleRepository;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Map<String, Object>> getUserOptions() {
        return userRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", u.getId());
                    m.put("username", u.getUsername());
                    m.put("nickname", u.getNickname());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getUsersByRole(String roleCode) {
        return userRepository.findByRole(roleCode, roleCode).stream()
                .map(u -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", u.getId());
                    m.put("username", u.getUsername());
                    m.put("nickname", u.getNickname());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> listUsers(String keyword, Pageable pageable) {
        Page<SysUser> users;
        if (StringUtils.hasText(keyword)) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(this::toDto);
    }

    @Override
    public UserDto getUserById(Long id) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toDto(user);
    }

    @Override
    public UserDto getCurrentUser(String username) {
        SysUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setOrganization(findOrganization(request.getOrganizationId()));

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<SysRole> roles = request.getRoleIds().stream()
                    .map(id -> roleRepository.findById(id)
                            .orElseThrow(() -> new BusinessException("角色不存在: " + id)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        userRepository.save(user);
        return toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        user.setOrganization(findOrganization(request.getOrganizationId()));

        if (request.getRoleIds() != null) {
            Set<SysRole> roles = request.getRoleIds().stream()
                    .map(rid -> roleRepository.findById(rid)
                            .orElseThrow(() -> new BusinessException("角色不存在: " + rid)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        userRepository.save(user);
        return toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserDto toDto(SysUser user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setNickname(user.getNickname());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        if (user.getOrganization() != null) {
            dto.setOrganizationId(user.getOrganization().getId());
            dto.setOrganizationName(user.getOrganization().getOrganizationName());
            dto.setOrganizationType(user.getOrganization().getOrganizationType());
        }

        Set<RoleDto> roleDtos = user.getRoles().stream().map(role -> {
            RoleDto rd = new RoleDto();
            rd.setId(role.getId());
            rd.setRoleName(role.getRoleName());
            rd.setRoleCode(role.getRoleCode());
            rd.setDescription(role.getDescription());
            Set<PermissionDto> permDtos = role.getPermissions().stream().map(perm -> {
                PermissionDto pd = new PermissionDto();
                pd.setId(perm.getId());
                pd.setPermName(perm.getPermName());
                pd.setPermCode(perm.getPermCode());
                pd.setType(perm.getType());
                pd.setPath(perm.getPath());
                pd.setIcon(perm.getIcon());
                pd.setSortOrder(perm.getSortOrder());
                pd.setParentId(perm.getParentId());
                return pd;
            }).collect(Collectors.toSet());
            rd.setPermissions(permDtos);
            return rd;
        }).collect(Collectors.toSet());
        dto.setRoles(roleDtos);

        return dto;
    }

    private Organization findOrganization(Long organizationId) {
        if (organizationId == null) {
            return null;
        }
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("机构不存在: " + organizationId));
    }
}
