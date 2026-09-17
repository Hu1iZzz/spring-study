package com.hh.example.study.repository;

import com.hh.example.study.entity.SysConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysConfigRepository extends JpaRepository<SysConfig, Long> {
    Optional<SysConfig> findByCode(String code);
    boolean existsByCode(String code);
}