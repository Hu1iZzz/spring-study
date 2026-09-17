package com.hh.example.study.service.impl;

import com.hh.example.study.entity.Department;
import com.hh.example.study.repository.DepartmentRepository;
import com.hh.example.study.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("部门不存在"));
    }

    @Override
    @Transactional
    public Department create(Department dept) {
        if (dept.getParentId() == null) dept.setParentId(0L);
        if (dept.getSortOrder() == null) dept.setSortOrder(0);
        if (dept.getStatus() == null) dept.setStatus(true);
        return departmentRepository.save(dept);
    }

    @Override
    @Transactional
    public Department update(Long id, Department dept) {
        Department existing = findById(id);
        existing.setName(dept.getName());
        existing.setParentId(dept.getParentId());
        existing.setSortOrder(dept.getSortOrder());
        existing.setStatus(dept.getStatus());
        return departmentRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        departmentRepository.deleteById(id);
    }
}