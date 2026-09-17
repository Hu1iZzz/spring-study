package com.hh.example.study.service.impl;

import com.hh.example.study.entity.Role;
import com.hh.example.study.entity.UserRole;
import com.hh.example.study.repository.RoleRepository;
import com.hh.example.study.repository.UserRoleRepository;
import com.hh.example.study.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("角色不存在"));
    }

    @Override
    @Transactional
    public Role create(Role role) {
        if (roleRepository.existsByCode(role.getCode())) {
            throw new RuntimeException("角色编码已存在");
        }
        if (role.getStatus() == null) role.setStatus(true);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role update(Long id, Role role) {
        Role existing = findById(id);
        existing.setName(role.getName());
        existing.setDescription(role.getDescription());
        existing.setStatus(role.getStatus());
        return roleRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public List<Long> findRoleIdsByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleRepository.deleteByUserId(userId);
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleRepository.save(ur);
            }
        }
    }
}