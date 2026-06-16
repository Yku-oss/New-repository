DROP DATABASE IF EXISTS newspaper;
CREATE DATABASE newspaper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE newspaper;

CREATE TABLE customer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    email VARCHAR(100),
    password VARCHAR(100),
    role VARCHAR(20) DEFAULT 'customer',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE supplier (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(255)
);

CREATE TABLE newspaper (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    period VARCHAR(50),
    stock INT DEFAULT 0,
    category_id INT,
    supplier_id INT,
    recommended TINYINT(1) DEFAULT 0,
    description TEXT,
    image_url VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES supplier(id) ON DELETE SET NULL
);

CREATE TABLE subscription (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    newspaper_id INT NOT NULL,
    quantity INT DEFAULT 1,
    total_price DECIMAL(10,2),
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    start_date DATE,
    end_date DATE,
    delivery_address VARCHAR(255),
    payment_method VARCHAR(50),
    payment_status VARCHAR(20) DEFAULT 'unpaid',
    approval_status VARCHAR(20) DEFAULT 'pending',
    status VARCHAR(20) DEFAULT 'pending',
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (newspaper_id) REFERENCES newspaper(id)
);

CREATE TABLE payment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subscription_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'success',
    FOREIGN KEY (subscription_id) REFERENCES subscription(id)
);

CREATE TABLE inventory_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    newspaper_id INT NOT NULL,
    change_quantity INT NOT NULL,
    type VARCHAR(10) NOT NULL,
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (newspaper_id) REFERENCES newspaper(id)
);

-- 测试数据
INSERT INTO customer (name, phone, address, email, password, role) VALUES
('管理员', '13800138000', '邮局大楼', 'admin@post.com', '123456', 'staff'),
('张三', '13912345678', '阳光小区1栋', 'zhangsan@mail.com', '123456', 'customer'),
('李四', '13787654321', '花园路2号', 'lisi@mail.com', '123456', 'customer');

INSERT INTO category (name, description) VALUES
('日报', '每日发行的报纸'),
('周报', '每周发行的报纸'),
('月刊', '每月发行的杂志类'),
('体育', '体育类报纸杂志'),
('财经', '财经类报纸杂志');

INSERT INTO supplier (name, contact_person, phone, address) VALUES
('人民日报社', '王经理', '010-12345678', '北京朝阳区'),
('南方日报社', '李经理', '020-87654321', '广州越秀区'),
('环球出版社', '陈经理', '021-55667788', '上海浦东区');

INSERT INTO newspaper (name, price, period, stock, category_id, supplier_id, recommended, description) VALUES
('人民日报', 1.50, '日报', 100, 1, 1, TRUE, '中国共产党中央委员会机关报'),
('南方周末', 3.00, '周报', 50, 2, 2, TRUE, '中国最具影响力的新闻周报之一'),
('财经杂志', 20.00, '月刊', 30, 5, 3, TRUE, '中国最具影响力的财经出版物'),
('体坛周报', 2.00, '周报', 80, 4, 2, FALSE, '中国发行量最大的体育类报纸'),
('光明日报', 1.50, '日报', 60, 1, 1, FALSE, '中国著名综合性日报');
