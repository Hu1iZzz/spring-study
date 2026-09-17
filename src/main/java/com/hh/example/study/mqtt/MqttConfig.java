package com.hh.example.study.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.StringUtils;

/**
 * MQTT 配置：连接工厂 + 入站（订阅）/出站（发布）适配器
 *
 * MQTT 核心概念（对照 RabbitMQ 学习）：
 * - broker：消息服务器（如 EMQX / Mosquitto），对应 RabbitMQ 本身
 * - client-id：客户端唯一标识，同一 broker 上重复会互相踢下线
 * - topic：主题，消息按主题路由（如 demo/topic），对应交换机+routing key
 * - QoS：服务质量。0 最多一次（可能丢）、1 至少一次（可能重）、2 恰好一次（最慢）
 * - retain：保留消息，broker 会把最后一条 retain 消息推给新订阅者
 *
 * 结构：Publisher -> mqttOutboundChannel -> broker <- mqttInboundChannel <- Subscriber
 */
@Configuration
public class MqttConfig {

    /** 入站通道名：broker -> 应用 */
    public static final String MQTT_INBOUND_CHANNEL = "mqttInboundChannel";
    /** 出站通道名：应用 -> broker */
    public static final String MQTT_OUTBOUND_CHANNEL = "mqttOutboundChannel";

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.username:}")
    private String username;

    @Value("${mqtt.password:}")
    private String password;

    @Value("${mqtt.default-topic}")
    private String defaultTopic;

    @Value("${mqtt.qos:1}")
    private int qos;

    /**
     * Paho 连接工厂：所有入站/出站适配器共用
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        if (StringUtils.hasText(username)) {
            options.setUserName(username);
        }
        if (StringUtils.hasText(password)) {
            options.setPassword(password.toCharArray());
        }
        // 会话不持久化，重连后需要重新订阅（由适配器自动完成）
        options.setCleanSession(true);
        // 断线自动重连
        options.setAutomaticReconnect(true);
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * 入站适配器：订阅 default-topic，收到的消息投递到 mqttInboundChannel
     * （Paho v3 的 adapter 内部就是 MqttClient.subscribe）
     */
    @Bean
    public MessageProducer mqttInboundAdapter(MqttPahoClientFactory mqttClientFactory) {
        // 入站单独用 client-id，避免和发布端互相挤掉线
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "-inbound", mqttClientFactory, defaultTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannelName(MQTT_INBOUND_CHANNEL);
        return adapter;
    }

    /**
     * 入站通道：消息从适配器流向订阅了该通道的 @ServiceActivator
     */
    @Bean(name = MQTT_INBOUND_CHANNEL)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    /**
     * 出站适配器：发送到 mqttOutboundChannel 的消息被发布到 broker
     */
    @Bean
    public MessageHandler mqttOutboundHandler(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler handler =
                new MqttPahoMessageHandler(clientId + "-outbound", mqttClientFactory);
        handler.setAsync(true);
        handler.setDefaultQos(qos);
        // 消息头里没带 topic 时，发布到这个默认主题
        handler.setDefaultTopic(defaultTopic);
        return handler;
    }

    /**
     * 出站通道
     */
    @Bean(name = MQTT_OUTBOUND_CHANNEL)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
}
