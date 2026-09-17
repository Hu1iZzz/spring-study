package com.hh.example.study.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "dict_data")
public class DictData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "value", nullable = false, length = 100)
    private String value;

    @ColumnDefault("0")
    @Column(name = "sort_order")
    private Integer sortOrder;

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
    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public Instant getCreateTime() { return createTime; }
    public void setCreateTime(Instant createTime) { this.createTime = createTime; }
    public Instant getUpdateTime() { return updateTime; }
    public void setUpdateTime(Instant updateTime) { this.updateTime = updateTime; }
}