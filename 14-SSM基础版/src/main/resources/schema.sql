-- H2 兼容 MySQL 模式
CREATE TABLE IF NOT EXISTS t_user (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    age         INT,
    email       VARCHAR(128),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

MERGE INTO t_user(id, name, age, email) KEY(id) VALUES (1, '张三', 24, 'zhangsan@example.com');
MERGE INTO t_user(id, name, age, email) KEY(id) VALUES (2, '李四', 30, 'lisi@example.com');
MERGE INTO t_user(id, name, age, email) KEY(id) VALUES (3, '王五', 28, 'wangwu@example.com');
