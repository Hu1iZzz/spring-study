package com.hh.example.study.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    public void recordLog(String username, HttpServletRequest request,
                          String operation, String status, String message) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        String logMsg = String.format(
            "[%s] 用户: %s, IP: %s, 操作: %s, 状态: %s, 消息: %s",
            LocalDateTime.now(), username, ip, operation, status, message
        );
        logger.info(logMsg);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}