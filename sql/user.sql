-- ==========================================
-- 表名：user（用户表）
-- 描述：存储系统用户信息
-- 版本：v1.0
-- ==========================================
CREATE TABLE `user` (
    -- ---------- 基础字段 ----------
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键，自增）',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号，唯一）',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密存储）',
    
    -- ---------- 个人信息 ----------
    `email` VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '用户昵称（显示名称）',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL地址',
    `gender` TINYINT(1) DEFAULT 0 COMMENT '性别（0：未知，1：男，2：女）',
    `age` INT(3) DEFAULT NULL COMMENT '年龄',
    
    -- ---------- 状态与安全 ----------
    `status` TINYINT(1) DEFAULT 1 COMMENT '账户状态（0：禁用，1：正常）',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP地址',
    
    -- ---------- 审计字段 ----------
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    
    -- ---------- 约束 ----------
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引',
    INDEX `idx_email` (`email`) COMMENT '邮箱查询索引',
    INDEX `idx_phone` (`phone`) COMMENT '手机号查询索引',
    INDEX `idx_status` (`status`) COMMENT '状态查询索引'
    
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='用户表（系统用户信息）';
