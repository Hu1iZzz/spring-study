package com.hh.example.study.service;

import com.hh.example.study.entity.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {
    List<Permission> findAll();
    List<Permission> findByRoleIds(List<Long> roleIds);
    Set<String> findCodesByUserId(Long userId);
    List<Long> findIdsByRoleId(Long roleId);
    void assignToRole(Long roleId, List<Long> permissionIds);
}