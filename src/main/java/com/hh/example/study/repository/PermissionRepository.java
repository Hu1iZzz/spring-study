package com.hh.example.study.repository;

import com.hh.example.study.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);
    List<Permission> findByType(String type);
    List<Permission> findByParentId(Long parentId);
}