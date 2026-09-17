package com.hh.example.study.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.stereotype.Component;

/**
 * MQTT 订阅者：处理从 broker 收到的消息
 *
 * 对应 RabbitMQ 示例里的 LoginMessageConsumer（@RabbitListener），
 * 这里换成监听 Spring Integration 通道 mqttInboundChannel。
 */
@Slf4j
@Component
public class MqttMessageSubscriber {

    /**
     * 订阅 demo/topic（或其他入站主题），收到消息后打日志。
     * 想做业务处理（如触发通知、落库）就在这里写。
     */
    @ServiceActivator(inputChannel = MqttConfig.MQTT_INBOUND_CHANNEL)
    public void handle(@Header(MqttHeaders.RECEIVED_TOPIC) String topic, Message<?> message) {
        Object payload = message.getPayload();
        // Paho 转换器默认把消息转成 String；字节数组时转一下
        String text = payload instanceof byte[] bytes ? new String(bytes) : String.valueOf(payload);
        log.info("[MQTT 订阅者] 收到消息 -> topic: {}, payload: {}", topic, text);

        if (text.startsWith("error:")) {
            // 演示异常场景：抛出异常时 QoS>0 的消息会重投，学习时可观察 broker 行为
            throw new MessagingException(message, "模拟处理失败");
        }
    }
}
