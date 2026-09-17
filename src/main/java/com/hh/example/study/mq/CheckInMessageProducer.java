package com.hh.example.study.mq;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 签到消息生产者：把签到消息发送到签到交换机
 */
@Component
@RequiredArgsConstructor
public class CheckInMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(CheckInMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void send(CheckInMessage message) {
        logger.info("[MQ 生产者] 发送签到消息: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CHECKIN_EXCHANGE,
                RabbitMQConfig.CHECKIN_ROUTING_KEY,
                message
        );
    }
}
