package com.hh.example.study.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "dict_type")
public class DictType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @ColumnDefault("1")
    @Column(name = "status")
    private Boolean status;

    @ColumnDefault("current_timestamp()")
    @Column(name = "create_time")
    private Instant createTime;

    @ColumnDefault("current_timestamp()")
    @Column(name = "update_time")
    private Instant updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public Instant getCreateTime() { return createTime; }
    public void setCreateTime(Instant createTime) { this.createTime = createTime; }
    public Instant getUpdateTime() { return updateTime; }
    public void setUpdateTime(Instant updateTime) { this.updateTime = updateTime; }
}