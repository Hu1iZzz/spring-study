package com.hh.example.study.repository;

import com.hh.example.study.entity.CheckInRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CheckInRecordRepository extends JpaRepository<CheckInRecord, Long> {

    /**
     * 查询用户在某个时间点之后的签到记录（用于判断今日是否已签到）
     */
    boolean existsByUsernameAndCheckInTimeAfter(String username, Instant time);

    /**
     * 按用户查询签到记录，最新在前
     */
    List<CheckInRecord> findByUsernameOrderByCheckInTimeDesc(String username);

    /**
     * 查询用户在时间区间 [start, end) 内的签到记录（用于月历展示）
     */
    List<CheckInRecord> findByUsernameAndCheckInTimeGreaterThanEqualAndCheckInTimeLessThan(
            String username, Instant start, Instant end);
}
