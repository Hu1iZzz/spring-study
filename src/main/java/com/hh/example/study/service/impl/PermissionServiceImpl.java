package com.hh.example.study.service.impl;

import com.hh.example.study.entity.Permission;
import com.hh.example.study.entity.RolePermission;
import com.hh.example.study.entity.UserRole;
import com.hh.example.study.repository.PermissionRepository;
import com.hh.example.study.repository.RolePermissionRepository;
import com.hh.example.study.repository.RoleRepository;
import com.hh.example.study.repository.UserRoleRepository;
import com.hh.example.study.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public List<Permission> findByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return Collections.emptyList();
        Set<Long> permIds = new HashSet<>();
        for (Long roleId : roleIds) {
            rolePermissionRepository.findByRoleId(roleId)
                    .forEach(rp -> permIds.add(rp.getPermissionId()));
        }
        if (permIds.isEmpty()) return Collections.emptyList();
        return permissionRepository.findAllById(permIds);
    }

    @Override
    public Set<String> findCodesByUserId(Long userId) {
        List<Long> roleIds = userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return Collections.emptySet();

        if (roleRepository.findAllById(roleIds).stream().anyMatch(r -> "ADMIN".equals(r.getCode()))) {
            return permissionRepository.findAll().stream()
                    .map(Permission::getCode)
                    .collect(Collectors.toSet());
        }

        return findByRoleIds(roleIds).stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Long> findIdsByRoleId(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignToRole(Long roleId, List<Long> permissionIds) {
        rolePermissionRepository.deleteByRoleId(roleId);
        if (permissionIds != null) {
            for (Long permId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                rolePermissionRepository.save(rp);
            }
        }
    }
}