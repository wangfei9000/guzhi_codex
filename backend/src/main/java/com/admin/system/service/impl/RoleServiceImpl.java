package com.admin.system.service.impl;

import com.admin.system.dto.CreateRoleRequest;
import com.admin.system.dto.PermissionDto;
import com.admin.system.dto.RoleDto;
import com.admin.system.entity.SysPermission;
import com.admin.system.entity.SysRole;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SysPermissionRepository;
import com.admin.system.repository.SysRoleRepository;
import com.admin.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleRepository roleRepository;
    private final SysPermissionRepository permissionRepository;

    @Override
    public List<RoleDto> listRoles() {
        return roleRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public RoleDto getRoleById(Long id) {
        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        return toDto(role);
    }

    @Override
    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<SysPermission> perms = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            role.setPermissions(perms);
        }

        roleRepository.save(role);
        return toDto(role);
    }

    @Override
    @Transactional
    public RoleDto updateRole(Long id, CreateRoleRequest request) {
        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());

        if (request.getPermissionIds() != null) {
            Set<SysPermission> perms = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            role.setPermissions(perms);
        }

        roleRepository.save(role);
        return toDto(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new BusinessException("角色不存在");
        }
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, Set<Long> permissionIds) {
        SysRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        Set<SysPermission> perms = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.setPermissions(perms);
        roleRepository.save(role);
    }

    private RoleDto toDto(SysRole role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleCode(role.getRoleCode());
        dto.setDescription(role.getDescription());
        dto.setCreatedAt(role.getCreatedAt());

        Set<PermissionDto> permDtos = role.getPermissions().stream().map(p -> {
            PermissionDto pd = new PermissionDto();
            pd.setId(p.getId());
            pd.setPermName(p.getPermName());
            pd.setPermCode(p.getPermCode());
            pd.setType(p.getType());
            return pd;
        }).collect(Collectors.toSet());
        dto.setPermissions(permDtos);

        return dto;
    }
}
