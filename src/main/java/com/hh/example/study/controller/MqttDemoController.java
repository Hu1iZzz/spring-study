package com.hh.example.study.controller;

import com.hh.example.study.mqtt.MqttMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * MQTT 示例接口：发布一条消息到 broker，同时会被自己的订阅者收到，走完闭环
 */
@RestController
@RequestMapping("/api/mqtt")
@RequiredArgsConstructor
public class MqttDemoController {

    private final MqttMessagePublisher mqttMessagePublisher;

    @PostMapping("/publish")
    public Map<String, Object> publish(@RequestParam String message,
                                       @RequestParam(required = false) String topic,
                                       @RequestParam(defaultValue = "false") boolean retain) {
        mqttMessagePublisher.publish(topic, message, retain);
        return Map.of(
                "code", 200,
                "msg", "消息已发布到 MQTT broker，请查看订阅者日志",
                "topic", topic == null ? "默认主题" : topic,
                "message", message,
                "retain", retain
        );
    }
}
