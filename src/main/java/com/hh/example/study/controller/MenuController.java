package com.hh.example.study.controller;

import com.hh.example.study.entity.Menu;
import com.hh.example.study.service.MenuService;
import com.hh.example.study.service.UserService;
import com.hh.example.study.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('menu:view')")
    public List<Menu> list() {
        return menuService.findAll();
    }

    @GetMapping("/tree")
    public List<Map<String, Object>> tree() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) return List.of();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) return List.of();
        return menuService.buildTree(menuService.findMenusByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('menu:view')")
    public Menu get(@PathVariable Long id) {
        return menuService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('menu:add')")
    public Menu create(@RequestBody Menu menu) {
        return menuService.create(menu);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('menu:edit')")
    public Menu update(@PathVariable Long id, @RequestBody Menu menu) {
        return menuService.update(id, menu);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('menu:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('role:view')")
    public List<Long> roleMenus(@PathVariable Long roleId) {
        return menuService.findMenuIdsByRoleId(roleId);
    }

    @PostMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('role:perm')")
    public ResponseEntity<Void> assign(@PathVariable Long roleId, @RequestBody Map<String, List<Long>> body) {
        menuService.assignToRole(roleId, body.get("menuIds"));
        return ResponseEntity.noContent().build();
    }
}