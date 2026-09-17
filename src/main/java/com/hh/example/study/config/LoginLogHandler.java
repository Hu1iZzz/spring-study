package com.hh.example.study.config;

import com.hh.example.study.entity.SysLog;
import com.hh.example.study.entity.User;
import com.hh.example.study.repository.UserRepository;
import com.hh.example.study.service.SysLogService;
import com.hh.example.study.service.impl.LogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class LoginLogHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Autowired
    private LogService logService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        String ip = getClientIp(request);

        logService.recordLog(username, request, "LOGIN_SUCCESS", "SUCCESS", "登录成功");

        SysLog sysLog = new SysLog();
        sysLog.setUsername(username);
        sysLog.setOperation("LOGIN");
        sysLog.setMethod("POST /api/auth/login");
        sysLog.setIp(ip);
        sysLog.setTimeMs(0L);
        sysLogService.create(sysLog);

        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLoginTime(Instant.now());
            user.setLastLoginIp(ip);
            userRepository.save(user);
        });

        response.sendRedirect("/index.html");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, @NonNull HttpServletResponse response,
                                        @NonNull AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        if (username == null || username.isEmpty()) {
            username = "未知用户";
        }
        logService.recordLog(username, request, "LOGIN_FAIL", "FAIL", "登录失败: " + exception.getMessage());
        response.sendRedirect("/login.html?error=true");
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
