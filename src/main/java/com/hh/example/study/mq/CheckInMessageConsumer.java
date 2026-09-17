package com.hh.example.study.mq;

import com.hh.example.study.entity.CheckInRecord;
import com.hh.example.study.repository.CheckInRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 签到消息消费者：监听签到队列，把签到记录写入数据库
 */
@Component
public class CheckInMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CheckInMessageConsumer.class);

    private final CheckInRecordRepository checkInRecordRepository;

    public CheckInMessageConsumer(CheckInRecordRepository checkInRecordRepository) {
        this.checkInRecordRepository = checkInRecordRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.CHECKIN_QUEUE)
    public void handleCheckInMessage(CheckInMessage message) {
        logger.info("[MQ 消费者] 收到签到消息 -> 用户: {}, IP: {}, 备注: {}, 发送时间: {}",
                message.getUsername(), message.getIp(), message.getRemark(), message.getSendTime());

        CheckInRecord record = new CheckInRecord();
        record.setUsername(message.getUsername());
        record.setIp(message.getIp());
        record.setRemark(message.getRemark());
        record.setCheckInTime(Instant.now());
        checkInRecordRepository.save(record);

        logger.info("[MQ 消费者] 签到记录已落库 -> id: {}, 用户: {}, 签到时间: {}",
                record.getId(), record.getUsername(), record.getCheckInTime());
    }
}
