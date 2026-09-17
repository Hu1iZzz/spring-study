package com.hh.example.study.service.impl;

import com.hh.example.study.entity.Asset;
import com.hh.example.study.repository.AssetRepository;
import com.hh.example.study.service.AssetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    @Override
    public Page<Asset> findAll(Pageable pageable) {
        return assetRepository.findAll(pageable);
    }

    @Override
    public Asset findById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("资产不存在"));
    }

    @Override
    @Transactional
    public Asset create(Asset asset) {
        if (assetRepository.existsByCode(asset.getCode())) {
            throw new RuntimeException("资产编号已存在");
        }
        if (asset.getStatus() == null) asset.setStatus("IDLE");
        return assetRepository.save(asset);
    }

    @Override
    @Transactional
    public Asset update(Long id, Asset asset) {
        Asset existing = findById(id);
        existing.setName(asset.getName());
        existing.setCategory(asset.getCategory());
        existing.setType(asset.getType());
        existing.setStatus(asset.getStatus());
        existing.setDepartmentId(asset.getDepartmentId());
        existing.setUserId(asset.getUserId());
        existing.setPurchaseDate(asset.getPurchaseDate());
        existing.setPurchasePrice(asset.getPurchasePrice());
        existing.setRemarks(asset.getRemarks());
        return assetRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        assetRepository.deleteById(id);
    }
}