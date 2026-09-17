package com.hh.example.study.controller;

import com.hh.example.study.entity.Role;
import com.hh.example.study.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('role:view')")
    public List<Role> list() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    public Role get(@PathVariable Long id) {
        return roleService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:add')")
    public Role create(@RequestBody Role role) {
        return roleService.create(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:edit')")
    public Role update(@PathVariable Long id, @RequestBody Role role) {
        return roleService.update(id, role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('role:view')")
    public List<Long> userRoles(@PathVariable Long userId) {
        return roleService.findRoleIdsByUserId(userId);
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('role:perm')")
    public ResponseEntity<Void> assign(@PathVariable Long userId, @RequestBody Map<String, List<Long>> body) {
        roleService.assignRoles(userId, body.get("roleIds"));
        return ResponseEntity.noContent().build();
    }
}
