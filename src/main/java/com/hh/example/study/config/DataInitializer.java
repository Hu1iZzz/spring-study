package com.hh.example.study.config;

import com.hh.example.study.entity.*;
import com.hh.example.study.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        initMenus();
        initRoleMenuRelations();
        initMenuPermissions();
        initAdminRole();
    }

    private void initMenus() {
        if (menuRepository.count() > 0) return;

        Menu home = saveMenu("首页", "/index.html", "HomeFilled", 0L, 1);
        Menu system = saveMenu("系统管理", null, "Setting", 0L, 2);
        Menu business = saveMenu("业务管理", null, "Briefcase", 0L, 3);

        saveMenu("用户管理", "/users.html", "User", system.getId(), 1);
        saveMenu("部门管理", "/department.html", "OfficeBuilding", system.getId(), 2);
        saveMenu("角色管理", "/role.html", "UserFilled", system.getId(), 3);
        saveMenu("菜单管理", "/menu.html", "Menu", system.getId(), 4);
        saveMenu("字典管理", "/dict.html", "Collection", system.getId(), 5);
        saveMenu("系统设置", "/config.html", "Tools", system.getId(), 6);

        saveMenu("资产管理", "/asset.html", "Box", business.getId(), 1);
        saveMenu("操作日志", "/log.html", "Document", business.getId(), 2);

        System.out.println("[DataInitializer] 菜单数据初始化完成，共 " + menuRepository.count() + " 条菜单");
    }

    private void initRoleMenuRelations() {
        List<Menu> allMenus = menuRepository.findAll();
        if (allMenus.isEmpty()) return;

        boolean assigned = false;
        for (Menu menu : allMenus) {
            assigned |= assignMenuIfMissing("ADMIN", menu.getId());
            assigned |= assignMenuIfMissing("USER", menu.getId());
        }
        if (assigned) {
            System.out.println("[DataInitializer] 角色-菜单关联初始化完成");
        }
    }

    private boolean assignMenuIfMissing(String roleCode, Long menuId) {
        return roleRepository.findByCode(roleCode).map(role -> {
            if (roleMenuRepository.findByRoleId(role.getId()).stream().noneMatch(rm -> rm.getMenuId().equals(menuId))) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                roleMenuRepository.save(rm);
                return true;
            }
            return false;
        }).orElse(false);
    }

    private void initMenuPermissions() {
        long before = permissionRepository.count();

        Permission userView = getOrCreatePerm("用户管理", "user:view", "MENU", "/users.html", 0L, 1, "User");
        getOrCreatePerm("新增用户", "user:add", "BUTTON", null, userView.getId(), 1, null);
        getOrCreatePerm("编辑用户", "user:edit", "BUTTON", null, userView.getId(), 2, null);
        getOrCreatePerm("删除用户", "user:delete", "BUTTON", null, userView.getId(), 3, null);

        Permission deptView = getOrCreatePerm("部门管理", "dept:view", "MENU", "/department.html", 0L, 2, "OfficeBuilding");
        getOrCreatePerm("新增部门", "dept:add", "BUTTON", null, deptView.getId(), 1, null);
        getOrCreatePerm("编辑部门", "dept:edit", "BUTTON", null, deptView.getId(), 2, null);
        getOrCreatePerm("删除部门", "dept:delete", "BUTTON", null, deptView.getId(), 3, null);

        Permission roleView = getOrCreatePerm("角色管理", "role:view", "MENU", "/role.html", 0L, 3, "UserFilled");
        getOrCreatePerm("新增角色", "role:add", "BUTTON", null, roleView.getId(), 1, null);
        getOrCreatePerm("编辑角色", "role:edit", "BUTTON", null, roleView.getId(), 2, null);
        getOrCreatePerm("删除角色", "role:delete", "BUTTON", null, roleView.getId(), 3, null);
        getOrCreatePerm("分配权限", "role:perm", "BUTTON", null, roleView.getId(), 4, null);

        Permission menuView = getOrCreatePerm("菜单管理", "menu:view", "MENU", "/menu.html", 0L, 4, "Menu");
        getOrCreatePerm("新增菜单", "menu:add", "BUTTON", null, menuView.getId(), 1, null);
        getOrCreatePerm("编辑菜单", "menu:edit", "BUTTON", null, menuView.getId(), 2, null);
        getOrCreatePerm("删除菜单", "menu:delete", "BUTTON", null, menuView.getId(), 3, null);

        Permission dictView = getOrCreatePerm("字典管理", "dict:view", "MENU", "/dict.html", 0L, 5, "Collection");
        getOrCreatePerm("新增字典", "dict:add", "BUTTON", null, dictView.getId(), 1, null);
        getOrCreatePerm("编辑字典", "dict:edit", "BUTTON", null, dictView.getId(), 2, null);
        getOrCreatePerm("删除字典", "dict:delete", "BUTTON", null, dictView.getId(), 3, null);

        Permission configView = getOrCreatePerm("系统设置", "config:view", "MENU", "/config.html", 0L, 6, "Tools");
        getOrCreatePerm("新增配置", "config:add", "BUTTON", null, configView.getId(), 1, null);
        getOrCreatePerm("编辑配置", "config:edit", "BUTTON", null, configView.getId(), 2, null);
        getOrCreatePerm("删除配置", "config:delete", "BUTTON", null, configView.getId(), 3, null);

        Permission assetView = getOrCreatePerm("资产管理", "asset:view", "MENU", "/asset.html", 0L, 7, "Box");
        getOrCreatePerm("新增资产", "asset:add", "BUTTON", null, assetView.getId(), 1, null);
        getOrCreatePerm("编辑资产", "asset:edit", "BUTTON", null, assetView.getId(), 2, null);
        getOrCreatePerm("删除资产", "asset:delete", "BUTTON", null, assetView.getId(), 3, null);

        getOrCreatePerm("操作日志", "log:view", "MENU", "/log.html", 0L, 8, "Document");

        roleRepository.findByCode("ADMIN").ifPresent(role -> {
            for (Permission p : permissionRepository.findAll()) {
                assignPermIfMissing(role.getId(), p.getId());
            }
        });
        roleRepository.findByCode("USER").ifPresent(role -> {
            permissionRepository.findByType("MENU").forEach(p -> assignPermIfMissing(role.getId(), p.getId()));
        });

        if (permissionRepository.count() > before) {
            System.out.println("[DataInitializer] 全部权限项初始化完成，共 " + permissionRepository.count() + " 条权限");
        }
    }

    private Permission getOrCreatePerm(String name, String code, String type, String path, Long parentId, int sortOrder, String icon) {
        return permissionRepository.findByCode(code).orElseGet(() -> savePerm(name, code, type, path, parentId, sortOrder, icon));
    }

    private void initAdminRole() {
        userRepository.findByUsername("admin").ifPresent(user -> {
            roleRepository.findByCode("ADMIN").ifPresent(role -> {
                if (userRoleRepository.findByUserId(user.getId()).stream().noneMatch(ur -> ur.getRoleId().equals(role.getId()))) {
                    UserRole ur = new UserRole();
                    ur.setUserId(user.getId());
                    ur.setRoleId(role.getId());
                    userRoleRepository.save(ur);
                    System.out.println("[DataInitializer] admin用户已关联ADMIN角色");
                }
            });
        });
    }

    private Menu saveMenu(String name, String path, String icon, Long parentId, int sortOrder) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPath(path);
        menu.setIcon(icon);
        menu.setParentId(parentId);
        menu.setSortOrder(sortOrder);
        menu.setStatus(true);
        return menuRepository.save(menu);
    }

    private Permission savePerm(String name, String code, String type, String path, Long parentId, int sortOrder, String icon) {
        Permission p = new Permission();
        p.setName(name);
        p.setCode(code);
        p.setType(type);
        p.setPath(path);
        p.setParentId(parentId);
        p.setSortOrder(sortOrder);
        p.setIcon(icon);
        return permissionRepository.save(p);
    }

    private void assignPermIfMissing(Long roleId, Long permId) {
        if (rolePermissionRepository.findByRoleId(roleId).stream().noneMatch(rp -> rp.getPermissionId().equals(permId))) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            rolePermissionRepository.save(rp);
        }
    }
}
