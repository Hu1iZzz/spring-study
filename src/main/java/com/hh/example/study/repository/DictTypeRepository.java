package com.hh.example.study.repository;

import com.hh.example.study.entity.DictType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DictTypeRepository extends JpaRepository<DictType, Long> {
    Optional<DictType> findByCode(String code);
    boolean existsByCode(String code);
}