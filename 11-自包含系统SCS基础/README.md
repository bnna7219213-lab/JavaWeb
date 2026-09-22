# SCS 基础版 - 自包含系统 (Self-Contained System)

## 项目概述

本项目演示 **SCS (Self-Contained System)** 微服务架构的基础概念——一个完整的自包含系统单元。

### 什么是 SCS？

**Self-Contained System** 是一种微服务架构风格，核心理念：

- **一个服务 = 一个完全自包含的系统单元**
- **每个 SCS 有自己的后端、数据库、前端**，能独立运行部署
- **服务间通过异步消息**（而非同步 HTTP）通信
- **每个 SCS 有明确的限界上下文**
- **前端集成**：SCS 页面 + API 直接集成

### 本项目的 SCS 单元：用户服务 (User SCS)

这是一个完全自包含的用户管理系统，包含：

| 组件 | 实现方式 | 路径/说明 |
|------|---------|----------|
| 前端 UI | Thymeleaf 模板 | `/user/` |
| 后端 API | Spring MVC REST | `/api/v1/users` |
| 数据存储 | ConcurrentHashMap 模拟 | 独立的 "USER_SCS_DB" |
| 消息系统 | Spring Events + 内部 Broker | Topic: `scs.user.*` |

## 技术栈

- **JDK 21**
- **Spring Boot 3.2.5**
- **Spring Web** (REST API + 嵌入式 Tomcat)
- **Spring Thymeleaf** (前端模板)
- **Spring Events** (进程内消息，模拟消息中间件)

## 项目结构

```
11-自包含系统SCS基础/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/scs/basic/
    │   ├── ScsBasicApplication.java          # 启动类
    │   ├── entity/
    │   │   ├── User.java                     # 用户实体 (领域模型)
    │   │   └── MessageRecord.java            # 消息记录实体
    │   ├── service/
    │   │   ├── UserService.java              # 用户业务服务
    │   │   ├── MessageService.java           # 消息中间件模拟
    │   │   └── UserEventService.java         # 事件发布/订阅
    │   └── controller/
    │       ├── HomeController.java           # 首页
    │       ├── UserPageController.java       # 页面控制器 (前端)
    │       └── UserApiController.java        # API 控制器 (REST)
    └── resources/
        ├── application.properties            # 配置文件 (端口: 8099)
        ├── templates/
        │   ├── index.html                     # 首页模板
        │   └── user/
        │       ├── index.html                 # 用户列表页
        │       ├── new.html                   # 创建用户页
        │       ├── messages.html              # 消息监控页
        │       └── scs-info.html              # SCS 系统信息页
        └── static/
            └── css/style.css                  # 样式文件
```

## 运行方式

### 1. 使用 Maven

```bash
cd 11-自包含系统SCS基础
mvn spring-boot:run
```

### 2. 打包运行

```bash
cd 11-自包含系统SCS基础
mvn clean package
java -jar target/scs-basic.jar
```

### 3. 使用 IDE

直接运行 `ScsBasicApplication.java` 中的 `main` 方法。

## 访问地址

启动后，访问以下地址：

| 地址 | 说明 |
|------|------|
| http://localhost:8099/ | 首页 (SCS 介绍) |
| http://localhost:8099/user/ | 用户管理页面 |
| http://localhost:8099/user/new | 创建用户页面 |
| http://localhost:8099/user/messages | 消息监控页面 |
| http://localhost:8099/user/scs-info | SCS 系统信息 |

## API 接口

### 基础路径: `/api/v1/users`

| 方法 | 路径 | 说明 | 请求体 |
|------|------|------|--------|
| GET | `/api/v1/users` | 查询所有用户 | - |
| GET | `/api/v1/users/{userId}` | 查询单个用户 | - |
| POST | `/api/v1/users` | 创建用户 | `{"username":"xxx","email":"xxx","phone":"xxx"}` |
| PUT | `/api/v1/users/{userId}` | 更新用户 | `{"email":"xxx","phone":"xxx"}` |
| DELETE | `/api/v1/users/{userId}` | 删除用户 | - |
| PATCH | `/api/v1/users/{userId}/status` | 变更状态 | `{"status":"ACTIVE"}` |

### API 调用示例

```bash
# 创建用户
curl -X POST http://localhost:8099/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username":"张三","email":"zhangsan@example.com","phone":"13800138000"}'

# 查询所有用户
curl http://localhost:8099/api/v1/users

# 查询单个用户 (替换 {userId})
curl http://localhost:8099/api/v1/users/{userId}

# 更新用户
curl -X PUT http://localhost:8099/api/v1/users/{userId} \
  -H "Content-Type: application/json" \
  -d '{"email":"newemail@example.com"}'

# 删除用户
curl -X DELETE http://localhost:8099/api/v1/users/{userId}

# 变更状态
curl -X PATCH http://localhost:8099/api/v1/users/{userId}/status \
  -H "Content-Type: application/json" \
  -d '{"status":"INACTIVE"}'
```

## SCS 架构演示

### 1. 自包含特性

整个用户服务是一个独立的 Spring Boot jar 包，包含：
- Thymeleaf 前端模板 (`templates/user/`)
- REST API 接口 (`/api/v1/users`)
- 自己的数据库 (`ConcurrentHashMap` 模拟)
- 自己的消息系统 (Spring Events)

### 2. 异步消息通信

用户创建后，系统自动发布 `UserCreated` 事件：

```java
// 1. 发布 Spring ApplicationEvent (进程内)
eventPublisher.publishEvent(new UserCreatedEvent(userId, username, email));

// 2. 同时写入 Message Broker (模拟发送到消息中间件)
messageService.publishMessage("scs.user.created", "UserCreated", payload);
```

### 3. 事件监听

```java
@EventListener
void onUserCreated(UserCreatedEvent event) {
    // 处理后续逻辑：发送欢迎邮件、初始化配置等
}
```

### 4. 数据隔离

User SCS 拥有完全独立的数据存储 (`ConcurrentHashMap`)，其他 SCS 无法直接访问。这是 SCS 的核心原则之一：**每个服务拥有自己的数据库**。

## 进阶学习

完成基础版后，请查看 **SCS 进阶版** 项目，了解：
- 多个 SCS 之间的协同工作
- 跨 SCS 事件通信 (User SCS + Order SCS)
- SCS 间的数据一致性
- 独立数据库前缀隔离
