package com.hh.example.study.repository;

import com.hh.example.study.entity.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SysLogRepository extends JpaRepository<SysLog, Long> {
    List<SysLog> findByOperationAndCreateTimeBetween(String operation, Instant start, Instant end);
}
