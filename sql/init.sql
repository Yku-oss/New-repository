-- =============================================
-- 邮局订报管理子系统 - 数据库初始化脚本
-- =============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS newspaper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE newspaper;

-- ===== 1. 客户表 =====
CREATE TABLE IF NOT EXISTS customer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '电话',
    address VARCHAR(255) COMMENT '地址',
    email VARCHAR(100) COMMENT '邮箱',
    password VARCHAR(100) COMMENT '密码',
    role VARCHAR(20) DEFAULT 'customer' COMMENT '角色: staff/customer',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='客户/用户表';

-- ===== 2. 报纸分类表 =====
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(255) COMMENT '分类描述'
) COMMENT='报纸分类表';

-- ===== 3. 供应商表 =====
CREATE TABLE IF NOT EXISTS supplier (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '地址'
) COMMENT='供应商表';

-- ===== 4. 报纸表 =====
CREATE TABLE IF NOT EXISTS newspaper (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT '报纸名称',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    period VARCHAR(50) COMMENT '刊期(日报/周报/月刊)',
    stock INT DEFAULT 0 COMMENT '库存数量',
    category_id INT COMMENT '分类ID',
    supplier_id INT COMMENT '供应商ID',
    recommended TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
    description TEXT COMMENT '描述',
    image_url VARCHAR(500) COMMENT '图片URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES supplier(id) ON DELETE SET NULL
) COMMENT='报纸表';

-- ===== 5. 订阅订单表 =====
CREATE TABLE IF NOT EXISTS subscription (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL COMMENT '客户ID',
    newspaper_id INT NOT NULL COMMENT '报纸ID',
    quantity INT DEFAULT 1 COMMENT '订阅数量',
    total_price DECIMAL(10,2) COMMENT '总价',
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    start_date DATE COMMENT '订阅开始日期',
    end_date DATE COMMENT '订阅结束日期',
    delivery_address VARCHAR(255) COMMENT '配送地址',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_status VARCHAR(20) DEFAULT '未支付' COMMENT '支付状态',
    approval_status VARCHAR(20) DEFAULT '待审批' COMMENT '审批状态',
    status VARCHAR(20) DEFAULT '待处理' COMMENT '订单状态',
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (newspaper_id) REFERENCES newspaper(id)
) COMMENT='订阅订单表';

-- ===== 6. 支付记录表 =====
CREATE TABLE IF NOT EXISTS payment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subscription_id INT NOT NULL COMMENT '订单ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method VARCHAR(50) COMMENT '支付方式(支付宝/微信/银行卡)',
    transaction_id VARCHAR(100) COMMENT '交易流水号',
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
    status VARCHAR(20) DEFAULT '成功' COMMENT '支付状态',
    FOREIGN KEY (subscription_id) REFERENCES subscription(id)
) COMMENT='支付记录表';

-- ===== 7. 库存变动日志表 =====
CREATE TABLE IF NOT EXISTS inventory_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    newspaper_id INT NOT NULL COMMENT '报纸ID',
    change_quantity INT NOT NULL COMMENT '变动数量(正数入库/负数出库)',
    type VARCHAR(10) NOT NULL COMMENT '类型: IN入库/OUT出库',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (newspaper_id) REFERENCES newspaper(id)
) COMMENT='库存变动日志表';

-- ===== 插入示例数据 =====
-- 客户
INSERT INTO customer (name, phone, address, email, password, role) VALUES
('管理员', '13800138000', '邮局大楼', 'admin@post.com', '123456', 'staff'),
('张三', '13912345678', '阳光小区1栋', 'zhangsan@mail.com', '123456', 'customer'),
('李四', '13787654321', '花园路2号', 'lisi@mail.com', '123456', 'customer');

-- 分类
INSERT INTO category (name, description) VALUES
('日报', '每日发行的报纸'),
('周报', '每周发行的报纸'),
('月刊', '每月发行的杂志类'),
('体育', '体育类报纸杂志'),
('财经', '财经类报纸杂志');

-- 供应商
INSERT INTO supplier (name, contact_person, phone, address) VALUES
('人民日报社', '王经理', '010-12345678', '北京朝阳区'),
('南方日报社', '李经理', '020-87654321', '广州越秀区'),
('环球出版社', '陈经理', '021-55667788', '上海浦东区');

-- 报纸
INSERT INTO newspaper (name, price, period, stock, category_id, supplier_id, recommended, description) VALUES
('人民日报', 1.50, '日报', 100, 1, 1, TRUE, '中国共产党中央委员会机关报'),
('南方周末', 3.00, '周报', 50, 2, 2, TRUE, '中国最具影响力的新闻周报之一'),
('财经杂志', 20.00, '月刊', 30, 5, 3, TRUE, '中国最具影响力的财经出版物'),
('体坛周报', 2.00, '周报', 80, 4, 2, FALSE, '中国发行量最大的体育类报纸'),
('光明日报', 1.50, '日报', 60, 1, 1, FALSE, '中国著名综合性日报');

-- 订阅订单
INSERT INTO subscription (customer_id, newspaper_id, quantity, total_price, start_date, end_date, delivery_address, payment_status, approval_status, status) VALUES
(2, 1, 1, 1.50, '2026-01-01', '2026-12-31', '阳光小区1栋', '已支付', '已通过', '已通过'),
(2, 3, 1, 20.00, '2026-01-01', '2026-06-30', '阳光小区1栋', '未支付', '待审批', '待处理'),
(3, 2, 2, 6.00, '2026-01-01', '2026-12-31', '花园路2号', '已支付', '已通过', '已通过');
