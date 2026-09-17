package com.hh.example.study.repository;

import com.hh.example.study.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {
    List<RoleMenu> findByRoleId(Long roleId);
    void deleteByRoleId(Long roleId);
}