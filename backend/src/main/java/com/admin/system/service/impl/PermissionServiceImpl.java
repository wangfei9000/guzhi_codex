package com.admin.system.service.impl;

import com.admin.system.dto.CreatePermissionRequest;
import com.admin.system.dto.PermissionDto;
import com.admin.system.entity.SysPermission;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SysPermissionRepository;
import com.admin.system.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionRepository permissionRepository;

    @Override
    public List<PermissionDto> getPermissionTree() {
        List<SysPermission> all = permissionRepository.findAll();
        Map<Long, List<SysPermission>> childrenMap = all.stream()
                .filter(p -> p.getParentId() != null)
                .collect(Collectors.groupingBy(SysPermission::getParentId));

        return all.stream()
                .filter(p -> p.getParentId() == null)
                .map(p -> buildTree(p, childrenMap))
                .collect(Collectors.toList());
    }

    private PermissionDto buildTree(SysPermission p, Map<Long, List<SysPermission>> childrenMap) {
        PermissionDto dto = toDto(p);
        List<SysPermission> children = childrenMap.getOrDefault(p.getId(), List.of());
        dto.setChildren(children.stream()
                .map(c -> buildTree(c, childrenMap))
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    @Transactional
    public PermissionDto createPermission(CreatePermissionRequest request) {
        SysPermission perm = new SysPermission();
        perm.setPermName(request.getPermName());
        perm.setPermCode(request.getPermCode());
        perm.setParentId(request.getParentId());
        perm.setType(request.getType());
        perm.setPath(request.getPath());
        perm.setIcon(request.getIcon());
        perm.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        permissionRepository.save(perm);
        return toDto(perm);
    }

    @Override
    @Transactional
    public PermissionDto updatePermission(Long id, CreatePermissionRequest request) {
        SysPermission perm = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        perm.setPermName(request.getPermName());
        perm.setPermCode(request.getPermCode());
        perm.setParentId(request.getParentId());
        perm.setType(request.getType());
        perm.setPath(request.getPath());
        perm.setIcon(request.getIcon());
        perm.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        permissionRepository.save(perm);
        return toDto(perm);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new BusinessException("权限不存在");
        }
        permissionRepository.deleteById(id);
    }

    private PermissionDto toDto(SysPermission p) {
        PermissionDto dto = new PermissionDto();
        dto.setId(p.getId());
        dto.setPermName(p.getPermName());
        dto.setPermCode(p.getPermCode());
        dto.setParentId(p.getParentId());
        dto.setType(p.getType());
        dto.setPath(p.getPath());
        dto.setIcon(p.getIcon());
        dto.setSortOrder(p.getSortOrder());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}
