package com.hh.example.study.controller;

import com.hh.example.study.entity.Permission;
import com.hh.example.study.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('role:view')")
    public List<Permission> list() {
        return permissionService.findAll();
    }

    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('role:view')")
    public List<Long> rolePermissions(@PathVariable Long roleId) {
        return permissionService.findIdsByRoleId(roleId);
    }

    @PostMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('role:perm')")
    public ResponseEntity<Void> assign(@PathVariable Long roleId, @RequestBody Map<String, List<Long>> body) {
        permissionService.assignToRole(roleId, body.get("permissionIds"));
        return ResponseEntity.noContent().build();
    }
}