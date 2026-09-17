-- ==========================================
-- 签到记录表
-- ==========================================
CREATE TABLE IF NOT EXISTS `check_in_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '签到IP',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '签到备注',
    `check_in_time` DATETIME NOT NULL COMMENT '签到时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_username_check_in_time` (`username`, `check_in_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';
