-- ==================== 创建数据库 ====================
CREATE DATABASE IF NOT EXISTS exercise_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE exercise_db;

-- ==================== 建表示例 ====================
-- 你可以根据业务需要修改这个表结构
CREATE TABLE IF NOT EXISTS 'order' (
    'id' bigint
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ==================== 插入测试数据 ====================
INSERT INTO user (username, password, email) VALUES
('张三', '123456', 'zhangsan@example.com'),
('李四', '123456', 'lisi@example.com');
