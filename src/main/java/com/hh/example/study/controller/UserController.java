package com.hh.example.study.controller;


import com.hh.example.study.dto.UserCreateDTO;
import com.hh.example.study.dto.UserDTO;
import com.hh.example.study.entity.User;
import com.hh.example.study.service.MenuService;
import com.hh.example.study.service.PermissionService;
import com.hh.example.study.service.RoleService;
import com.hh.example.study.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final MenuService menuService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/currentUser")
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
                UserDTO dto = new UserDTO(user);
                List<Long> roleIds = roleService.findRoleIdsByUserId(user.getId());
                dto.setRoles(roleIds.stream().map(String::valueOf).collect(Collectors.toList()));
                Set<String> permCodes = permissionService.findCodesByUserId(user.getId());
                dto.setPermissions(new ArrayList<>(permCodes));
                dto.setMenus(menuService.buildTree(menuService.findMenusByUserId(user.getId())));
                return dto;
            }
        }
        return null;
    }

    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserCreateDTO createDTO) {
        User user = userService.createUser(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDTO(user));
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(new UserDTO(user));
    }

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(new UserDTO(user));
    }

    /**
     * 分页查询所有用户
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:view')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        Page<User> userPage = userService.findAll(pageable);
        Page<UserDTO> dtoPage = userPage.map(UserDTO::new);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:edit')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO updateDTO) {
        User user = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(new UserDTO(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 禁用用户
     */
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 启用用户
     */
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }
}