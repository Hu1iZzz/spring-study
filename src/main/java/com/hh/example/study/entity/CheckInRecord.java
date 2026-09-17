package com.hh.example.study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

/**
 * 签到记录（由 MQ 消费者异步写入）
 */
@Entity
@Table(name = "check_in_record")
public class CheckInRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "check_in_time", nullable = false)
    private Instant checkInTime;

    @ColumnDefault("current_timestamp()")
    @Column(name = "create_time")
    private Instant createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Instant getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Instant checkInTime) { this.checkInTime = checkInTime; }

    public Instant getCreateTime() { return createTime; }
    public void setCreateTime(Instant createTime) { this.createTime = createTime; }
}
