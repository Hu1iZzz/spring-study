package com.hh.example.study.service.impl;

import com.hh.example.study.entity.SysLog;
import com.hh.example.study.repository.SysLogRepository;
import com.hh.example.study.service.SysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysLogServiceImpl implements SysLogService {

    private final SysLogRepository sysLogRepository;

    @Override
    public Page<SysLog> findAll(Pageable pageable) {
        return sysLogRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public SysLog create(SysLog log) {
        // 不设置时间的话 JPA 会显式插入 NULL，覆盖掉数据库的 CURRENT_TIMESTAMP 默认值
        if (log.getCreateTime() == null) {
            log.setCreateTime(Instant.now());
        }
        return sysLogRepository.save(log);
    }
}