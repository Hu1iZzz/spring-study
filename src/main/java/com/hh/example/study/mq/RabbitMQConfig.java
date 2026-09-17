package com.hh.example.study.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置：声明交换机、队列、绑定，并使用 JSON 序列化消息
 *
 * 结构：Producer -> login.exchange -> (routing key: login.notify) -> login.queue -> Consumer
 */
@Configuration
public class RabbitMQConfig {

    public static final String LOGIN_EXCHANGE = "login.exchange";
    public static final String LOGIN_QUEUE = "login.queue";
    public static final String LOGIN_ROUTING_KEY = "login.notify";

    public static final String CHECKIN_EXCHANGE = "checkin.exchange";
    public static final String CHECKIN_QUEUE = "checkin.queue";
    public static final String CHECKIN_ROUTING_KEY = "checkin.record";

    /** 直接型交换机：按 routing key 精确匹配投递 */
    @Bean
    public DirectExchange loginExchange() {
        return new DirectExchange(LOGIN_EXCHANGE, true, false);
    }

    /** 队列：持久化，服务重启后消息不丢失 */
    @Bean
    public Queue loginQueue() {
        return new Queue(LOGIN_QUEUE, true);
    }

    /** 将队列绑定到交换机 */
    @Bean
    public Binding loginBinding(Queue loginQueue, DirectExchange loginExchange) {
        return BindingBuilder.bind(loginQueue).to(loginExchange).with(LOGIN_ROUTING_KEY);
    }

    /** 签到交换机 */
    @Bean
    public DirectExchange checkInExchange() {
        return new DirectExchange(CHECKIN_EXCHANGE, true, false);
    }

    /** 签到队列：消费者在此异步落库 */
    @Bean
    public Queue checkInQueue() {
        return new Queue(CHECKIN_QUEUE, true);
    }

    /** 将签到队列绑定到签到交换机 */
    @Bean
    public Binding checkInBinding(Queue checkInQueue, DirectExchange checkInExchange) {
        return BindingBuilder.bind(checkInQueue).to(checkInExchange).with(CHECKIN_ROUTING_KEY);
    }

    /** 消息体使用 Jackson 3（Spring Boot 4 默认）转 JSON，替代默认的 JDK 序列化 */
    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
