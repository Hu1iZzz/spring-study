package com.hh.example.study.mq;

import com.hh.example.study.notify.WeComWebhookNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息消费者：监听登录通知队列，异步处理（写日志 + 推送企业微信）
 */
@Component
public class LoginMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(LoginMessageConsumer.class);

    private final WeComWebhookNotifier weComWebhookNotifier;

    public LoginMessageConsumer(WeComWebhookNotifier weComWebhookNotifier) {
        this.weComWebhookNotifier = weComWebhookNotifier;
    }

    @RabbitListener(queues = RabbitMQConfig.LOGIN_QUEUE)
    public void handleLoginMessage(LoginMessage message) {
        // 实际项目中这里可以调用通知服务：发邮件、发短信、写站内信等
        logger.info("[MQ 消费者] 收到登录通知 -> 用户: {}, IP: {}, 内容: {}, 发送时间: {}",
                message.getUsername(), message.getIp(), message.getContent(), message.getSendTime());

        // 同步推送到企业微信群（未配置 webhook key 时自动跳过）
        weComWebhookNotifier.sendMarkdown(String.format(
                "**登录通知**\n> 用户：<font color=\"info\">%s</font>\n> IP：%s\n> 时间：%s",
                message.getUsername(), message.getIp(), message.getSendTime()));
    }
}
