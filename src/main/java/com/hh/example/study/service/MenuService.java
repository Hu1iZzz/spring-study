package com.hh.example.study.service;

import com.hh.example.study.entity.Menu;

import java.util.List;
import java.util.Map;

public interface MenuService {
    List<Menu> findAll();
    Menu findById(Long id);
    Menu create(Menu menu);
    Menu update(Long id, Menu menu);
    void delete(Long id);

    List<Menu> findMenusByUserId(Long userId);
    List<Long> findMenuIdsByRoleId(Long roleId);
    void assignToRole(Long roleId, List<Long> menuIds);

    List<Map<String, Object>> buildTree(List<Menu> menus);
}