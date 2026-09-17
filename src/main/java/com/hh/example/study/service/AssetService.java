package com.hh.example.study.service;

import com.hh.example.study.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetService {
    Page<Asset> findAll(Pageable pageable);
    Asset findById(Long id);
    Asset create(Asset asset);
    Asset update(Long id, Asset asset);
    void delete(Long id);
}