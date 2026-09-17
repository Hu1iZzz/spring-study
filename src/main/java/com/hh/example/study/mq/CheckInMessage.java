package com.hh.example.study.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 签到消息体（页面发起签到后通过 RabbitMQ 异步落库）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInMessage implements Serializable {

    /** 用户名 */
    private String username;

    /** 签到 IP */
    private String ip;

    /** 签到备注 */
    private String remark;

    /** 发送时间 */
    private LocalDateTime sendTime;
}
