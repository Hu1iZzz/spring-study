package com.hh.example.study.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "sys_log")
public class SysLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "operation", length = 50)
    private String operation;

    @Column(name = "method", length = 200)
    private String method;

    @Column(name = "params", columnDefinition = "TEXT")
    private String params;

    @Column(name = "time_ms")
    private Long timeMs;

    @Column(name = "ip", length = 50)
    private String ip;

    @ColumnDefault("current_timestamp()")
    @Column(name = "create_time")
    private Instant createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
    public Long getTimeMs() { return timeMs; }
    public void setTimeMs(Long timeMs) { this.timeMs = timeMs; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public Instant getCreateTime() { return createTime; }
    public void setCreateTime(Instant createTime) { this.createTime = createTime; }
}