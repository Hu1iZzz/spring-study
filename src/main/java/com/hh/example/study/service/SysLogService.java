package com.hh.example.study.service;

import com.hh.example.study.entity.SysLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SysLogService {
    Page<SysLog> findAll(Pageable pageable);
    SysLog create(SysLog log);
}