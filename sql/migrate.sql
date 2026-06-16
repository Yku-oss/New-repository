-- 数据库迁移脚本
USE newspaper;

-- 添加 customer 表新字段（忽略已存在的列错误）
ALTER TABLE customer ADD COLUMN email VARCHAR(100) AFTER address;
ALTER TABLE customer ADD COLUMN password VARCHAR(100) AFTER email;
ALTER TABLE customer ADD COLUMN role VARCHAR(20) DEFAULT 'customer' AFTER password;
ALTER TABLE customer ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP AFTER role;

-- 添加 newspaper 表新字段
ALTER TABLE newspaper ADD COLUMN category_id INT AFTER stock;
ALTER TABLE newspaper ADD COLUMN supplier_id INT AFTER category_id;
ALTER TABLE newspaper ADD COLUMN recommended TINYINT(1) DEFAULT 0 AFTER supplier_id;
ALTER TABLE newspaper ADD COLUMN description TEXT AFTER recommended;
ALTER TABLE newspaper ADD COLUMN image_url VARCHAR(500) AFTER description;
ALTER TABLE newspaper ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP AFTER image_url;

-- 添加 subscription 表新字段
ALTER TABLE subscription ADD COLUMN start_date DATE AFTER order_time;
ALTER TABLE subscription ADD COLUMN end_date DATE AFTER start_date;
ALTER TABLE subscription ADD COLUMN delivery_address VARCHAR(255) AFTER end_date;
ALTER TABLE subscription ADD COLUMN payment_method VARCHAR(50) AFTER delivery_address;
ALTER TABLE subscription ADD COLUMN payment_status VARCHAR(20) DEFAULT '未支付' AFTER payment_method;
ALTER TABLE subscription ADD COLUMN approval_status VARCHAR(20) DEFAULT '待审批' AFTER payment_status;

-- 创建新表
CREATE TABLE IF NOT EXISTS category (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS supplier (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  contact_person VARCHAR(50),
  phone VARCHAR(20),
  address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS payment (
  id INT AUTO_INCREMENT PRIMARY KEY,
  subscription_id INT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  payment_method VARCHAR(50),
  transaction_id VARCHAR(100),
  payment_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(20) DEFAULT '成功',
  FOREIGN KEY (subscription_id) REFERENCES subscription(id)
);

CREATE TABLE IF NOT EXISTS inventory_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  newspaper_id INT NOT NULL,
  change_quantity INT NOT NULL,
  type VARCHAR(10) NOT NULL,
  remark VARCHAR(255),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (newspaper_id) REFERENCES newspaper(id)
);

-- 插入测试数据
INSERT IGNORE INTO customer (name, phone, address, email, password, role) VALUES
('管理员', '13800138000', '邮局大楼', 'admin@post.com', '123456', 'staff'),
('张三', '13912345678', '阳光小区1栋', 'zhangsan@mail.com', '123456', 'customer'),
('李四', '13787654321', '花园路2号', 'lisi@mail.com', '123456', 'customer');

INSERT IGNORE INTO category (id, name, description) VALUES
(1, '日报', '每日发行的报纸'),
(2, '周报', '每周发行的报纸'),
(3, '月刊', '每月发行的杂志类'),
(4, '体育', '体育类报纸杂志'),
(5, '财经', '财经类报纸杂志');

INSERT IGNORE INTO supplier (id, name, contact_person, phone, address) VALUES
(1, '人民日报社', '王经理', '010-12345678', '北京朝阳区'),
(2, '南方日报社', '李经理', '020-87654321', '广州越秀区'),
(3, '环球出版社', '陈经理', '021-55667788', '上海浦东区');
