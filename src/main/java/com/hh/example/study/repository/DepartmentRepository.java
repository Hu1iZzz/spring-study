package com.hh.example.study.repository;

import com.hh.example.study.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByParentId(Long parentId);
    boolean existsByName(String name);
}