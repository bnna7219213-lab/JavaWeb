# SCS 进阶版 - 多SCS协同 (Self-Contained System)

## 项目概述

本项目演示 **SCS (Self-Contained System)** 微服务架构的进阶概念——多个自包含系统单元之间的协同工作。

### 包含两个独立SCS单元

| SCS 单元 | 职责 | 路径前缀 | API前缀 | 数据库 |
|----------|------|---------|---------|--------|
| **User SCS** | 用户注册、查询、管理 | `/user-scs/` | `/user-scs/api/v1/users` | USER_SCS_DB |
| **Order SCS** | 订单创建、查询、关连 | `/order-scs/` | `/order-scs/api/v1/orders` | ORDER_SCS_DB |

### 跨SCS异步通信

```
用户注册 (User SCS)
    |
    v
存储到 USER_SCS_DB
    |
    v
发布 UserCreated 事件
    |
    v
消息中间件 (ScsMessageBroker 模拟 Kafka)
    |
    v
Order SCS @EventListener 消费事件
    |
    v
自动创建默认订单 -> 存储到 ORDER_SCS_DB
```

## 技术栈

- **JDK 21**
- **Spring Boot 3.2.5**
- **Spring Web** (REST API + 嵌入式 Tomcat)
- **Spring Thymeleaf** (前端模板)
- **Spring Events** (模拟跨SCS异步消息传递)
- **自定义 ScsMessageBroker** (模拟 Kafka 消息中间件)

## 项目结构

```
11-自包含系统SCS进阶/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/scs/advanced/
    │   ├── ScsAdvancedApplication.java           # 启动类
    │   ├── entity/
    │   │   ├── user/ScsUser.java                 # User SCS的领域模型
    │   │   └── order/ScsOrder.java               # Order SCS的领域模型
    │   ├── service/
    │   │   ├── user/
    │   │   │   ├── ScsUserService.java           # User SCS 业务服务
    │   │   │   └── UserScsEventPublisher.java    # User SCS 事件发布器
    │   │   └── order/
    │   │       └── ScsOrderService.java          # Order SCS 业务服务 (含事件监听)
    │   ├── controller/
    │   │   ├── HomeController.java               # 首页
    │   │   ├── user/
    │   │   │   ├── UserScsPageController.java    # User SCS 页面控制器
    │   │   │   └── UserScsApiController.java     # User SCS API 控制器
    │   │   ├── order/
    │   │   │   ├── OrderScsPageController.java   # Order SCS 页面控制器
    │   │   │   └── OrderScsApiController.java    # Order SCS API 控制器
    │   │   └── BrokerMonitorController.java      # 消息中间件监控
    │   ├── event/
    │   │   ├── UserCreatedEvent.java             # 用户创建事件
    │   │   ├── UserDeletedEvent.java             # 用户删除事件
    │   │   └── OrderCreatedEvent.java            # 订单创建事件
    │   ├── messaging/
    │   │   └── ScsMessageBroker.java             # 消息中间件模拟
    │   └── config/                               # (空目录，扩展用)
    └── resources/
        ├── application.properties                # 配置文件 (端口: 8100)
        ├── templates/
        │   ├── index.html                         # 首页
        │   ├── user/
        │   │   ├── index.html                     # 用户列表页
        │   │   ├── new.html                       # 创建用户页
        │   │   └── orders.html                    # 用户关联订单页
        │   ├── order/
        │   │   ├── index.html                     # 订单列表页
        │   │   ├── new.html                       # 创建订单页
        │   │   └── detail.html                    # 订单详情页
        │   └── broker/
        │       └── monitor.html                   # 消息中间件监控页
        └── static/
            └── css/style.css                      # 样式文件
```

## 运行方式

### 1. 使用 Maven

```bash
cd 11-自包含系统SCS进阶
mvn spring-boot:run
```

### 2. 打包运行

```bash
cd 11-自包含系统SCS进阶
mvn clean package
java -jar target/scs-advanced.jar
```

### 3. 使用 IDE

直接运行 `ScsAdvancedApplication.java` 中的 `main` 方法。

## 访问地址

### 主入口

| 地址 | 说明 |
|------|------|
| http://localhost:8100/ | 总览页 (SCS 架构说明) |
| http://localhost:8100/broker/ | 消息中间件监控 |

### User SCS (用户服务)

| 地址 | 说明 |
|------|------|
| http://localhost:8100/user-scs/ | 用户管理列表 |
| http://localhost:8100/user-scs/new | 创建用户 (触发跨SCS事件) |
| http://localhost:8100/user-scs/{userId}/orders | 查看用户关联订单 |

### Order SCS (订单服务)

| 地址 | 说明 |
|------|------|
| http://localhost:8100/order-scs/ | 订单管理列表 |
| http://localhost:8100/order-scs/new | 手动创建订单 |
| http://localhost:8100/order-scs/{orderId} | 订单详情 |

## API 接口

### User SCS API (`/user-scs/api/v1/users`)

| 方法 | 路径 | 说明 | 请求体 |
|------|------|------|--------|
| GET | `/user-scs/api/v1/users` | 查询所有用户 | - |
| GET | `/user-scs/api/v1/users/{userId}` | 查询单个用户 | - |
| POST | `/user-scs/api/v1/users` | **创建用户** (触发跨SCS流程) | `{"username":"xxx","email":"xxx","phone":"xxx"}` |
| PUT | `/user-scs/api/v1/users/{userId}` | 更新用户 | `{"email":"xxx","phone":"xxx"}` |
| DELETE | `/user-scs/api/v1/users/{userId}` | **删除用户** (触发跨SCS流程) | - |
| PATCH | `/user-scs/api/v1/users/{userId}/status` | 变更状态 | `{"status":"ACTIVE"}` |

### Order SCS API (`/order-scs/api/v1/orders`)

| 方法 | 路径 | 说明 | 请求体 |
|------|------|------|--------|
| GET | `/order-scs/api/v1/orders` | 查询所有订单 | - |
| GET | `/order-scs/api/v1/orders/{orderId}` | 查询单个订单 | - |
| GET | `/order-scs/api/v1/orders/user/{userId}` | 查询用户订单 | - |
| POST | `/order-scs/api/v1/orders` | 手动创建订单 | `{"userId":"xxx","username":"xxx","description":"xxx"}` |

### API 调用示例

```bash
# === User SCS API ===

# 创建用户 -> 自动触发跨SCS流程 -> Order SCS创建默认订单
curl -X POST http://localhost:8100/user-scs/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username":"张三","email":"zhangsan@example.com","phone":"13800138000"}'

# 查询所有用户
curl http://localhost:8100/user-scs/api/v1/users

# 查询单个用户
curl http://localhost:8100/user-scs/api/v1/users/USR-XXXXXXXXXXXX

# 删除用户 -> 自动触发UserDeleted事件 -> Order SCS清理关联订单
curl -X DELETE http://localhost:8100/user-scs/api/v1/users/USR-XXXXXXXXXXXX

# === Order SCS API ===

# 查询所有订单 (包含自动创建的默认订单 + 手动创建的订单)
curl http://localhost:8100/order-scs/api/v1/orders

# 查询用户的订单
curl http://localhost:8100/order-scs/api/v1/orders/user/USR-XXXXXXXXXXXX

# 手动创建订单
curl -X POST http://localhost:8100/order-scs/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":"USR-XXXXXXXXXXXX","username":"张三","description":"测试订单"}'
```

## 快速体验跨SCS协同

### 步骤1：启动应用

```bash
cd 11-自包含系统SCS进阶
mvn spring-boot:run
```

### 步骤2：创建用户 (触发跨SCS通信)

访问 http://localhost:8100/user-scs/new 创建用户，或使用:

```bash
curl -X POST http://localhost:8100/user-scs/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username":"测试用户","email":"test@scs.demo","phone":"13900139000"}'
```

### 步骤3：查看跨SCS效果

1. 返回 http://localhost:8100/user-scs/ 查看用户列表
2. 访问 http://localhost:8100/order-scs/ 查看自动创建的默认订单
3. 访问 http://localhost:8100/broker/ 查看消息中间件中的消息记录

### 步骤4：清理演示

删除用户后，访问 Order SCS 查看关联订单是否被自动清理。

## SCS 架构核心实现

### 1. 跨SCS事件监听

```java
// Order SCS 监听 User SCS 发布的事件
@EventListener
public void onUserCreated(UserCreatedEvent event) {
    // 为新用户创建默认订单
    createDefaultOrder(event.getUserId(), event.getUsername());
}
```

### 2. 数据隔离

- **User SCS** 使用 `USER_SCS_DB` (ConcurrentHashMap) - 仅 User SCS 可访问
- **Order SCS** 使用 `ORDER_SCS_DB` (ConcurrentHashMap) - 仅 Order SCS 可访问
- 两个数据库完全隔离，互不影响

### 3. 消息中间件 (ScsMessageBroker)

模拟 Kafka 的核心功能：
- Topic 管理 (`scs.events.user.created`, `scs.events.user.deleted`, `scs.events.order.created`)
- 消息持久化
- 发布/订阅模式
- 事件路由

### 4. 事件发布

```java
// User SCS 发布事件
eventPublisher.publishEvent(new UserCreatedEvent(
    this, user.getUserId(), user.getUsername(), user.getEmail()
));
// -> ScsMessageBroker 监听并路由到 topic
// -> Order SCS @EventListener 消费并处理
```

## 设计说明

### 为何在同一个 jar 中？

为了演示方便，两个 SCS 在同一个 Spring Boot 应用中运行。在实际生产部署中：

1. **User SCS** 应是一个独立的 jar，运行在端口 8081
2. **Order SCS** 应是一个独立的 jar，运行在端口 8082
3. **消息中间件** 使用真实的 Kafka/RabbitMQ 集群
4. **数据库** 每个 SCS 使用独立的数据库实例
5. **前端** 每个 SCS 的前端部署在不同子域名

### 这里模拟了什么？

- `ConcurrentHashMap` -> 模拟独立数据库
- `ApplicationEvent` -> 模拟 Kafka 消息
- `ScsMessageBroker` -> 模拟分布式消息代理
- 同一应用不同路径 -> 模拟不同服务的子域名
