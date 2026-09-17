package com.hh.example.study.service;

import com.hh.example.study.entity.SysConfig;

import java.util.List;

public interface SysConfigService {
    List<SysConfig> findAll();
    SysConfig findById(Long id);
    SysConfig create(SysConfig config);
    SysConfig update(Long id, SysConfig config);
    void delete(Long id);
}