-- ==========================================
-- 后台管理系统数据库脚本
-- ==========================================

-- 部门表
CREATE TABLE IF NOT EXISTS `department` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID(0=根)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) COMMENT '描述',
    `status` TINYINT(1) DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联';

-- 资产表
CREATE TABLE IF NOT EXISTS `asset` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL COMMENT '资产编号',
    `name` VARCHAR(100) NOT NULL COMMENT '资产名称',
    `category` VARCHAR(20) NOT NULL COMMENT '类型(IT/FIXED)',
    `type` VARCHAR(50) COMMENT '规格型号',
    `status` VARCHAR(20) DEFAULT 'IDLE' COMMENT '状态(IDLE/IN_USE/REPAIR/SCRAPPED)',
    `department_id` BIGINT COMMENT '所属部门',
    `user_id` BIGINT COMMENT '使用人',
    `purchase_date` DATE COMMENT '购置日期',
    `purchase_price` DECIMAL(10,2) COMMENT '购置价格',
    `remarks` VARCHAR(500) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) COMMENT '操作人',
    `operation` VARCHAR(50) COMMENT '操作类型',
    `method` VARCHAR(200) COMMENT '请求方法',
    `params` TEXT COMMENT '请求参数',
    `time_ms` BIGINT COMMENT '耗时(ms)',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

-- 字典类型表
CREATE TABLE IF NOT EXISTS `dict_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '字典名称',
    `code` VARCHAR(50) NOT NULL COMMENT '字典编码',
    `status` TINYINT(1) DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型';

-- 字典数据表
CREATE TABLE IF NOT EXISTS `dict_data` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `type_id` BIGINT NOT NULL COMMENT '字典类型ID',
    `label` VARCHAR(100) NOT NULL COMMENT '显示文本',
    `value` VARCHAR(100) NOT NULL COMMENT '字典值',
    `sort_order` INT DEFAULT 0,
    `status` TINYINT(1) DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '参数名称',
    `code` VARCHAR(100) NOT NULL COMMENT '参数编码',
    `value` VARCHAR(500) COMMENT '参数值',
    `description` VARCHAR(200) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';

-- 给user表加部门字段
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `department_id` BIGINT COMMENT '部门ID';

-- 权限表（菜单+按钮）
CREATE TABLE IF NOT EXISTS `permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `type` VARCHAR(10) NOT NULL DEFAULT 'MENU' COMMENT 'MENU菜单/BUTTON按钮',
    `path` VARCHAR(100) COMMENT '前端路由(菜单用)',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
    `sort_order` INT DEFAULT 0,
    `icon` VARCHAR(50) COMMENT '图标',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

-- 菜单表（独立于权限，支持多层级树形）
CREATE TABLE IF NOT EXISTS `menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `path` VARCHAR(100) COMMENT '路由路径(空=父菜单)',
    `icon` VARCHAR(50) COMMENT '图标',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID(0=根)',
    `sort_order` INT DEFAULT 0,
    `status` TINYINT(1) DEFAULT 1 COMMENT '0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联';

-- ==========================================
-- 初始化数据
-- ==========================================

INSERT IGNORE INTO `role` (`name`, `code`, `description`) VALUES
    ('管理员', 'ADMIN', '系统管理员'),
    ('普通用户', 'USER', '普通用户'),
    ('游客', 'GUEST', '未登录访客(仅参考,实际不登录)');

-- 权限项：菜单 + 按钮
INSERT IGNORE INTO `permission` (`name`, `code`, `type`, `path`, `parent_id`, `sort_order`, `icon`) VALUES
    ('首页', 'index', 'MENU', '/index.html', 0, 1, 'HomeFilled'),
    ('用户管理', 'user:view', 'MENU', '/users.html', 0, 2, 'User'),
    ('新增用户', 'user:add', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='user:view'), 1, NULL),
    ('编辑用户', 'user:edit', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='user:view'), 2, NULL),
    ('删除用户', 'user:delete', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='user:view'), 3, NULL),
    ('部门管理', 'dept:view', 'MENU', '/department.html', 0, 3, 'OfficeBuilding'),
    ('新增部门', 'dept:add', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='dept:view'), 1, NULL),
    ('编辑部门', 'dept:edit', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='dept:view'), 2, NULL),
    ('删除部门', 'dept:delete', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='dept:view'), 3, NULL),
    ('角色管理', 'role:view', 'MENU', '/role.html', 0, 4, 'UserFilled'),
    ('新增角色', 'role:add', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='role:view'), 1, NULL),
    ('编辑角色', 'role:edit', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='role:view'), 2, NULL),
    ('删除角色', 'role:delete', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='role:view'), 3, NULL),
    ('分配权限', 'role:perm', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='role:view'), 4, NULL),
    ('菜单管理', 'menu:view', 'MENU', '/menu.html', 0, 5, 'Menu'),
    ('新增菜单', 'menu:add', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='menu:view'), 1, NULL),
    ('编辑菜单', 'menu:edit', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='menu:view'), 2, NULL),
    ('删除菜单', 'menu:delete', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='menu:view'), 3, NULL),
    ('资产管理', 'asset:view', 'MENU', '/asset.html', 0, 6, 'Box'),
    ('新增资产', 'asset:add', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='asset:view'), 1, NULL),
    ('编辑资产', 'asset:edit', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='asset:view'), 2, NULL),
    ('删除资产', 'asset:delete', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='asset:view'), 3, NULL),
    ('操作日志', 'log:view', 'MENU', '/log.html', 0, 7, 'Document'),
    ('字典管理', 'dict:view', 'MENU', '/dict.html', 0, 8, 'Collection'),
    ('新增字典', 'dict:add', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='dict:view'), 1, NULL),
    ('编辑字典', 'dict:edit', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='dict:view'), 2, NULL),
    ('删除字典', 'dict:delete', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='dict:view'), 3, NULL),
    ('系统设置', 'config:view', 'MENU', '/config.html', 0, 9, 'Setting'),
    ('新增配置', 'config:add', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='config:view'), 1, NULL),
    ('编辑配置', 'config:edit', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='config:view'), 2, NULL),
    ('删除配置', 'config:delete', 'BUTTON', NULL, (SELECT id FROM permission WHERE code='config:view'), 3, NULL);

-- 管理员：全部权限
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `role` r, `permission` p WHERE r.code='ADMIN';

-- 普通用户：所有查看权限(只读)
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `role` r, `permission` p WHERE r.code='USER' AND p.type='MENU';

-- ==========================================
-- 菜单初始化数据（多层级树形）
-- ==========================================
INSERT IGNORE INTO `menu` (`name`, `path`, `icon`, `parent_id`, `sort_order`) VALUES
    ('首页', '/index.html', 'HomeFilled', 0, 1),
    ('系统管理', NULL, 'Setting', 0, 2),
    ('业务管理', NULL, 'Briefcase', 0, 3);

-- 系统管理子菜单
INSERT IGNORE INTO `menu` (`name`, `path`, `icon`, `parent_id`, `sort_order`) VALUES
    ('用户管理', '/users.html', 'User', (SELECT id FROM menu WHERE name='系统管理'), 1),
    ('部门管理', '/department.html', 'OfficeBuilding', (SELECT id FROM menu WHERE name='系统管理'), 2),
    ('角色管理', '/role.html', 'UserFilled', (SELECT id FROM menu WHERE name='系统管理'), 3),
    ('菜单管理', '/menu.html', 'Menu', (SELECT id FROM menu WHERE name='系统管理'), 4),
    ('字典管理', '/dict.html', 'Collection', (SELECT id FROM menu WHERE name='系统管理'), 5),
    ('系统设置', '/config.html', 'Tools', (SELECT id FROM menu WHERE name='系统管理'), 6);

-- 业务管理子菜单
INSERT IGNORE INTO `menu` (`name`, `path`, `icon`, `parent_id`, `sort_order`) VALUES
    ('资产管理', '/asset.html', 'Box', (SELECT id FROM menu WHERE name='业务管理'), 1),
    ('操作日志', '/log.html', 'Document', (SELECT id FROM menu WHERE name='业务管理'), 2);

-- 管理员：全部菜单
INSERT IGNORE INTO `role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id FROM `role` r, `menu` m WHERE r.code='ADMIN';

-- 普通用户：全部菜单（只读）
INSERT IGNORE INTO `role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id FROM `role` r, `menu` m WHERE r.code='USER';

INSERT IGNORE INTO `dict_type` (`name`, `code`) VALUES
    ('资产类型', 'asset_category'),
    ('资产状态', 'asset_status');

INSERT IGNORE INTO `dict_data` (`type_id`, `label`, `value`, `sort_order`) VALUES
    ((SELECT id FROM dict_type WHERE code='asset_category'), 'IT资产', 'IT', 1),
    ((SELECT id FROM dict_type WHERE code='asset_category'), '固定资产', 'FIXED', 2),
    ((SELECT id FROM dict_type WHERE code='asset_status'), '空闲', 'IDLE', 1),
    ((SELECT id FROM dict_type WHERE code='asset_status'), '使用中', 'IN_USE', 2),
    ((SELECT id FROM dict_type WHERE code='asset_status'), '维修中', 'REPAIR', 3),
    ((SELECT id FROM dict_type WHERE code='asset_status'), '已报废', 'SCRAPPED', 4);

INSERT IGNORE INTO `sys_config` (`name`, `code`, `value`, `description`) VALUES
    ('系统名称', 'sys_name', '学习平台后台管理系统', '系统显示名称'),
    ('默认分页大小', 'page_size', '10', '列表默认每页条数');

-- 给admin用户分配管理员角色
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `user` u, `role` r WHERE u.username='admin' AND r.code='ADMIN';