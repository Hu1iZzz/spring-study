package com.hh.example.study.controller;

import com.hh.example.study.mq.LoginMessage;
import com.hh.example.study.mq.LoginMessageProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MQ 示例接口：触发一条登录通知消息走完 生产者 -> 交换机 -> 队列 -> 消费者 全流程
 */
@RestController
@RequestMapping("/api/mq")
@RequiredArgsConstructor
public class MqDemoController {

    private final LoginMessageProducer loginMessageProducer;

    @PostMapping("/login-notify")
    public Map<String, Object> sendLoginNotify(@RequestParam String username,
                                               HttpServletRequest request) {
        LoginMessage message = new LoginMessage(
                username,
                request.getRemoteAddr(),
                "您刚刚登录了系统",
                LocalDateTime.now()
        );
        loginMessageProducer.send(message);

        return Map.of(
                "code", 200,
                "msg", "消息已发送到 RabbitMQ，请查看消费者日志",
                "message", message
        );
    }
}
