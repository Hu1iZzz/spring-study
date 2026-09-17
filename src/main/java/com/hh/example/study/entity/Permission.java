package com.hh.example.study.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @ColumnDefault("'MENU'")
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "path", length = 100)
    private String path;

    @ColumnDefault("0")
    @Column(name = "parent_id")
    private Long parentId;

    @ColumnDefault("0")
    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "icon", length = 50)
    private String icon;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}