package com.hh.example.study.service.impl;

import com.hh.example.study.entity.Menu;
import com.hh.example.study.entity.RoleMenu;
import com.hh.example.study.entity.UserRole;
import com.hh.example.study.repository.MenuRepository;
import com.hh.example.study.repository.RoleMenuRepository;
import com.hh.example.study.repository.RoleRepository;
import com.hh.example.study.repository.UserRoleRepository;
import com.hh.example.study.service.MenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {

    private static final String CACHE_KEY = "menu:all";

    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<Menu> findAll() {
        // Redis 未启动时降级为直接查库，不能让缓存故障影响主流程
        String cached = null;
        try {
            cached = redisTemplate.opsForValue().get(CACHE_KEY);
        } catch (Exception ignored) {}
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<Menu>>() {});
            } catch (Exception ignored) {}
        }
        List<Menu> menus = menuRepository.findAll();
        try {
            redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(menus));
        } catch (Exception ignored) {}
        return menus;
    }

    @Override
    public Menu findById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("菜单不存在"));
    }

    @Override
    @Transactional
    public Menu create(Menu menu) {
        if (menu.getParentId() == null) menu.setParentId(0L);
        if (menu.getSortOrder() == null) menu.setSortOrder(0);
        if (menu.getStatus() == null) menu.setStatus(true);
        Menu saved = menuRepository.save(menu);
        redisTemplate.delete(CACHE_KEY);
        return saved;
    }

    @Override
    @Transactional
    public Menu update(Long id, Menu menu) {
        Menu existing = findById(id);
        existing.setName(menu.getName());
        existing.setPath(menu.getPath());
        existing.setIcon(menu.getIcon());
        existing.setParentId(menu.getParentId());
        existing.setSortOrder(menu.getSortOrder());
        existing.setStatus(menu.getStatus());
        Menu saved = menuRepository.save(existing);
        redisTemplate.delete(CACHE_KEY);
        return saved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        menuRepository.deleteById(id);
        redisTemplate.delete(CACHE_KEY);
    }

    @Override
    public List<Menu> findMenusByUserId(Long userId) {
        List<Long> roleIds = userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return Collections.emptyList();

        if (roleRepository.findAllById(roleIds).stream().anyMatch(r -> "ADMIN".equals(r.getCode()))) {
            return findAll().stream()
                    .filter(m -> Boolean.TRUE.equals(m.getStatus()))
                    .collect(Collectors.toList());
        }

        Set<Long> menuIds = new HashSet<>();
        for (Long roleId : roleIds) {
            roleMenuRepository.findByRoleId(roleId)
                    .forEach(rm -> menuIds.add(rm.getMenuId()));
        }
        if (menuIds.isEmpty()) return Collections.emptyList();
        return menuRepository.findAllById(menuIds).stream()
                .filter(m -> Boolean.TRUE.equals(m.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findMenuIdsByRoleId(Long roleId) {
        return roleMenuRepository.findByRoleId(roleId).stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignToRole(Long roleId, List<Long> menuIds) {
        roleMenuRepository.deleteByRoleId(roleId);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuRepository.save(rm);
            }
        }
    }

    @Override
    public List<Map<String, Object>> buildTree(List<Menu> menus) {
        Map<Long, Map<String, Object>> nodeMap = new LinkedHashMap<>();
        for (Menu m : menus) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", m.getId());
            node.put("name", m.getName());
            node.put("path", m.getPath());
            node.put("icon", m.getIcon());
            node.put("parentId", m.getParentId());
            node.put("sortOrder", m.getSortOrder());
            node.put("status", m.getStatus());
            node.put("children", new ArrayList<>());
            nodeMap.put(m.getId(), node);
        }

        List<Map<String, Object>> roots = new ArrayList<>();
        for (Menu m : menus) {
            Map<String, Object> node = nodeMap.get(m.getId());
            if (m.getParentId() != null && m.getParentId() > 0 && nodeMap.containsKey(m.getParentId())) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) nodeMap.get(m.getParentId()).get("children");
                children.add(node);
            } else {
                roots.add(node);
            }
        }

        sortTree(roots);
        return roots;
    }

    @SuppressWarnings("unchecked")
    private void sortTree(List<Map<String, Object>> nodes) {
        nodes.sort(Comparator.comparingInt(n -> {
            Object sort = n.get("sortOrder");
            return sort instanceof Number ? ((Number) sort).intValue() : 0;
        }));
        for (Map<String, Object> node : nodes) {
            sortTree((List<Map<String, Object>>) node.get("children"));
        }
    }
}