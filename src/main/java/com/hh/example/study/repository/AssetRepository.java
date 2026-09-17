package com.hh.example.study.repository;

import com.hh.example.study.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByCode(String code);
    boolean existsByCode(String code);
}