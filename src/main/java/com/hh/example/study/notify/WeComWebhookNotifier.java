package com.hh.example.study.notify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * 企业微信群机器人推送服务
 *
 * 使用方法：
 * 1. 建一个企业微信群 -> 群设置 -> 群机器人 -> 添加，复制 Webhook 地址里的 key
 * 2. 把 key 配到 application.yaml 的 wechat.webhook-key
 * 3. 个人微信关注企业微信息后，进群即可在微信里收到推送
 *
 * 文档：https://developer.work.weixin.qq.com/document/path/91770
 */
@Slf4j
@Service
public class WeComWebhookNotifier {

    private static final String WEBHOOK_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send";

    private final RestClient restClient;
    private final String webhookKey;

    public WeComWebhookNotifier(@Value("${wechat.webhook-key:}") String webhookKey) {
        this.restClient = RestClient.create();
        this.webhookKey = webhookKey;
    }

    /** 是否已配置机器人 key */
    public boolean isEnabled() {
        return webhookKey != null && !webhookKey.isBlank();
    }

    /**
     * 推送 markdown 消息到企业微信群，失败只记日志不影响主流程
     */
    public void sendMarkdown(String content) {
        if (!isEnabled()) {
            log.info("[微信推送] 未配置 wechat.webhook-key，跳过推送: {}", content);
            return;
        }
        try {
            Map<?, ?> resp = restClient.post()
                    .uri(WEBHOOK_URL + "?key=" + webhookKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "msgtype", "markdown",
                            "markdown", Map.of("content", content)
                    ))
                    .retrieve()
                    .body(Map.class);
            log.info("[微信推送] 结果: {}", resp);
        } catch (Exception e) {
            log.error("[微信推送] 发送失败: {}", e.getMessage());
        }
    }
}
