package com.hh.example.study.mq;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息生产者：把登录通知消息发送到交换机
 */
@Component
@RequiredArgsConstructor
public class LoginMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(LoginMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void send(LoginMessage message) {
        logger.info("[MQ 生产者] 发送登录通知消息: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LOGIN_EXCHANGE,
                RabbitMQConfig.LOGIN_ROUTING_KEY,
                message
        );
    }
}
