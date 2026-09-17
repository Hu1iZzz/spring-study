package com.hh.example.study.service.impl;

import com.hh.example.study.entity.SysConfig;
import com.hh.example.study.repository.SysConfigRepository;
import com.hh.example.study.service.SysConfigService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigRepository sysConfigRepository;

    @Override
    public List<SysConfig> findAll() {
        return sysConfigRepository.findAll();
    }

    @Override
    public SysConfig findById(Long id) {
        return sysConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("配置不存在"));
    }

    @Override
    @Transactional
    public SysConfig create(SysConfig config) {
        if (sysConfigRepository.existsByCode(config.getCode())) {
            throw new RuntimeException("配置编码已存在");
        }
        return sysConfigRepository.save(config);
    }

    @Override
    @Transactional
    public SysConfig update(Long id, SysConfig config) {
        SysConfig existing = findById(id);
        existing.setName(config.getName());
        existing.setValue(config.getValue());
        existing.setDescription(config.getDescription());
        return sysConfigRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sysConfigRepository.deleteById(id);
    }
}