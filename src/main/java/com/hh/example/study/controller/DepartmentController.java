package com.hh.example.study.controller;

import com.hh.example.study.entity.Department;
import com.hh.example.study.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('dept:view')")
    public List<Department> list() {
        return departmentService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:view')")
    public Department get(@PathVariable Long id) {
        return departmentService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('dept:add')")
    public Department create(@RequestBody Department dept) {
        return departmentService.create(dept);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:edit')")
    public Department update(@PathVariable Long id, @RequestBody Department dept) {
        return departmentService.update(id, dept);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
