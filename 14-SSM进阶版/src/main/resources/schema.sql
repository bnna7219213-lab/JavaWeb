CREATE TABLE IF NOT EXISTS t_user (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    age         INT          DEFAULT 0,
    email       VARCHAR(128),
    phone       VARCHAR(32),
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_order (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    order_no    VARCHAR(64)  NOT NULL,
    user_id     INT          NOT NULL,
    product     VARCHAR(128) NOT NULL,
    amount      DECIMAL(10,2) DEFAULT 0,
    status      VARCHAR(16)  DEFAULT 'PENDING',
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

MERGE INTO t_user(id, name, age, email, phone) KEY(id) VALUES (1, '张三', 24, 'zhang@example.com', '13800001111');
MERGE INTO t_user(id, name, age, email, phone) KEY(id) VALUES (2, '李四', 30, 'li@example.com', '13800002222');
MERGE INTO t_user(id, name, age, email, phone) KEY(id) VALUES (3, '王五', 28, 'wang@example.com', '13800003333');

MERGE INTO t_order(id, order_no, user_id, product, amount, status) KEY(id) VALUES (1, 'ORD2024001', 1, 'MacBook Pro', 14999.00, 'PAID');
MERGE INTO t_order(id, order_no, user_id, product, amount, status) KEY(id) VALUES (2, 'ORD2024002', 1, 'iPhone 15 Pro', 8999.00, 'PAID');
MERGE INTO t_order(id, order_no, user_id, product, amount, status) KEY(id) VALUES (3, 'ORD2024003', 2, 'AirPods Pro', 1899.00, 'PENDING');
MERGE INTO t_order(id, order_no, user_id, product, amount, status) KEY(id) VALUES (4, 'ORD2024004', 3, 'iPad Air', 4799.00, 'SHIPPED');
