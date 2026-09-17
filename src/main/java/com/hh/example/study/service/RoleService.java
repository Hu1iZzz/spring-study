package com.hh.example.study.service;

import com.hh.example.study.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();
    Role findById(Long id);
    Role create(Role role);
    Role update(Long id, Role role);
    void delete(Long id);
    List<Long> findRoleIdsByUserId(Long userId);
    void assignRoles(Long userId, List<Long> roleIds);
}