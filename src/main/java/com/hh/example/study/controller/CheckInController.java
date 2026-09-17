package com.hh.example.study.controller;

import com.hh.example.study.entity.CheckInRecord;
import com.hh.example.study.mq.CheckInMessage;
import com.hh.example.study.mq.CheckInMessageProducer;
import com.hh.example.study.repository.CheckInRecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 签到接口：签到请求通过 RabbitMQ 异步处理并落库
 */
@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    private final CheckInMessageProducer checkInMessageProducer;
    private final CheckInRecordRepository checkInRecordRepository;

    public CheckInController(CheckInMessageProducer checkInMessageProducer,
                             CheckInRecordRepository checkInRecordRepository) {
        this.checkInMessageProducer = checkInMessageProducer;
        this.checkInRecordRepository = checkInRecordRepository;
    }

    /** 发起签到：把签到消息投递到 MQ，由消费者异步写入数据库 */
    @PostMapping
    public Map<String, Object> checkIn(@RequestParam(required = false) String remark,
                                       Authentication authentication,
                                       HttpServletRequest request) {
        String username = authentication.getName();

        CheckInMessage message = new CheckInMessage(
                username,
                request.getRemoteAddr(),
                remark,
                LocalDateTime.now()
        );
        checkInMessageProducer.send(message);

        return Map.of(
                "code", 200,
                "msg", "签到消息已发送，正在异步处理",
                "message", message
        );
    }

    /** 查询当前用户今天是否已签到 */
    @GetMapping("/today")
    public Map<String, Object> today(Authentication authentication) {
        String username = authentication.getName();
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        boolean checkedIn = checkInRecordRepository.existsByUsernameAndCheckInTimeAfter(username, startOfDay);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("checkedIn", checkedIn);
        return result;
    }

    /** 查询当前用户的签到记录 */
    @GetMapping("/records")
    public Map<String, Object> records(Authentication authentication) {
        String username = authentication.getName();
        List<CheckInRecord> records = checkInRecordRepository.findByUsernameOrderByCheckInTimeDesc(username);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("total", records.size());
        result.put("records", records);
        return result;
    }

    /**
     * 查询某个月有签到记录的日期（供首页日历渲染）
     *
     * @param year  年，默认当年
     * @param month 月（1-12），默认当月
     */
    @GetMapping("/calendar")
    public Map<String, Object> calendar(@RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) Integer month,
                                        Authentication authentication) {
        LocalDate today = LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;

        // 月初（含）到下月初（不含），按系统时区换算成 Instant 再查库
        ZoneId zone = ZoneId.systemDefault();
        Instant start = LocalDate.of(y, m, 1).atStartOfDay(zone).toInstant();
        Instant end = LocalDate.of(y, m, 1).plusMonths(1).atStartOfDay(zone).toInstant();

        List<CheckInRecord> records = checkInRecordRepository
                .findByUsernameAndCheckInTimeGreaterThanEqualAndCheckInTimeLessThan(authentication.getName(), start, end);

        // 去重（同一天可能签到多次）并按日期排序
        List<String> days = records.stream()
                .map(r -> LocalDateTime.ofInstant(r.getCheckInTime(), zone).toLocalDate().toString())
                .distinct()
                .sorted()
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("year", y);
        result.put("month", m);
        result.put("days", days);
        result.put("total", days.size());
        return result;
    }
}
