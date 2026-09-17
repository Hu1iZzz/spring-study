package com.hh.example.study.repository;

import com.hh.example.study.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByParentId(Long parentId);
    List<Menu> findByParentIdAndStatusTrueOrderBySortOrderAsc(Long parentId);
}