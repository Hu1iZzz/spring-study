package com.hh.example.study.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录通知消息体（通过 RabbitMQ 在服务内部异步传递）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginMessage implements Serializable {

    /** 用户名 */
    private String username;

    /** 登录 IP */
    private String ip;

    /** 消息内容 */
    private String content;

    /** 发送时间 */
    private LocalDateTime sendTime;
}
