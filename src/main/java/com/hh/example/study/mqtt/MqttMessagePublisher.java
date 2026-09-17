package com.hh.example.study.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

/**
 * MQTT 发布者：把消息发送到 broker
 *
 * 对应 RabbitMQ 示例里的 LoginMessageProducer（RabbitTemplate.convertAndSend），
 * 这里通过 mqttOutboundChannel 交给出站适配器发布。
 */
@Slf4j
@Service
public class MqttMessagePublisher {

    private final MessageChannel mqttOutboundChannel;
    private final String defaultTopic;

    public MqttMessagePublisher(
            @org.springframework.beans.factory.annotation.Qualifier(MqttConfig.MQTT_OUTBOUND_CHANNEL)
            MessageChannel mqttOutboundChannel,
            @Value("${mqtt.default-topic}") String defaultTopic) {
        this.mqttOutboundChannel = mqttOutboundChannel;
        this.defaultTopic = defaultTopic;
    }

    /**
     * 发布消息到指定主题
     *
     * @param topic  主题；传 null 时使用默认主题
     * @param payload 消息内容
     * @param retain 是否为保留消息：broker 会保存最后一条，新订阅者立即收到
     */
    public void publish(String topic, String payload, boolean retain) {
        String targetTopic = (topic == null || topic.isBlank()) ? defaultTopic : topic;
        mqttOutboundChannel.send(MessageBuilder.withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, targetTopic)
                .setHeader(MqttHeaders.RETAINED, retain)
                .build());
        log.info("[MQTT 发布者] 已发布 -> topic: {}, payload: {}, retain: {}", targetTopic, payload, retain);
    }
}
