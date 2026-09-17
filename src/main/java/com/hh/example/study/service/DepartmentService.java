package com.hh.example.study.service;

import com.hh.example.study.entity.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> findAll();
    Department findById(Long id);
    Department create(Department dept);
    Department update(Long id, Department dept);
    void delete(Long id);
}