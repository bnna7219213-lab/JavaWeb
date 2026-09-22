# Spring Modulith 模块化单体 - 进阶版

## 项目概述

本项目演示了使用 Spring Boot 3.3 + JDK 21 构建完整的模块化单体应用（Modular Monolith）进阶架构。

相比基础版，进阶版包含：
- **5个模块**：user, product, order, notification, audit
- **事件驱动解耦**：通过 `@EventListener` 实现跨模块零耦合通信
- **ApplicationModule 概念**：明确的 API 包和 Internal 包边界
- **完整的依赖倒置（DIP）**：高层模块通过接口调用低层模块
- **16项模块验证测试**：覆盖包结构、封装性、无循环依赖、事件驱动等

## 技术栈

- JDK 21
- Spring Boot 3.3.5
- Spring Web (端口: 8102)
- Spring Event (ApplicationEvent / @EventListener)
- Thymeleaf 模板引擎
- Lombok

## 五模块架构

```
┌──────────────────────────────────────────────────────────────────────┐
│                    Spring Application Context                          │
│                                                                       │
│   基础模块                     上游模块                     事件驱动模块     │
│   ┌──────────┐             ┌──────────┐             ┌──────────────┐ │
│   │   User   │───DIP────→│  Order   │───事件──→    │ Notification │ │
│   │  Module  │   接口依赖  │  Module  │   发布      │   Module     │ │
│   │          │             │          │            │ (@Event       │ │
│   │ User API │←──DIP────│ Order API│            │  Listener)   │ │
│   │          │             │          │            └──────────────┘ │
│   │ 内部:     │             │          │            ┌──────────────┐ │
│   │ service/ │             │          │───事件──→  │    Audit     │ │
│   │ internal │             │          │   发布     │   Module     │ │
│   └──────────┘             │          │            │ (@Event       │ │
│   ┌──────────┐             │          │            │  Listener)   │ │
│   │ Product  │───DIP────→│          │            └──────────────┘ │
│   │  Module  │   接口依赖  └──────────┘                              │
│   │          │                                                       │
│   │ Product  │                                                       │
│   │   API    │                                                       │
│   └──────────┘                                                       │
└──────────────────────────────────────────────────────────────────────┘
```

| 模块 | 类型 | 职责 | 依赖 | 发布事件 |
|------|------|------|------|---------|
| user | 基础模块 | 用户管理 | 无 | UserEvent |
| product | 基础模块 | 商品管理 | 无 | 无 |
| order | 上游模块 | 订单管理 | UserModule + ProductModule（接口） | OrderEvent |
| notification | 事件驱动 | 通知服务 | 无（监听事件） | 无 |
| audit | 事件驱动 | 审计服务 | 无（监听事件） | 无 |

## 核心设计原则

### 1. 模块边界（ApplicationModule）

每个模块有严格的包边界：

```
modules/user/
├── api/                          ← 对外API（接口定义）
│   └── UserModule.java
├── service/internal/             ← 内部实现（封装）
│   └── UserServiceImpl.java
├── controller/                   ← Web控制层
│   └── UserController.java
└── entity/                       ← 内部数据结构（封装）
    └── UserEntity.java
```

### 2. 依赖倒置原则（DIP）

```java
// OrderServiceImpl - 依赖接口，不依赖实现
@Service
public class OrderServiceImpl implements OrderModule {
    private final UserModule userModule;      // 接口
    private final ProductModule productModule; // 接口

    public OrderServiceImpl(UserModule userModule, ProductModule productModule, ...) {
        this.userModule = userModule;          // Spring注入实现
        this.productModule = productModule;    // Spring注入实现
    }
}
```

### 3. 事件驱动通信

```java
// === 发布事件（Order模块）===
eventPublisher.publishEvent(OrderEvent.created(orderId, userId, productId, amount));

// === 监听事件（Notification模块 - 完全不知道OrderService的存在） ===
@EventListener
public void onOrderCreated(OrderEvent event) {
    if (OrderEvent.ORDER_CREATED.equals(event.getEventType())) {
        notifyUser(event.getUserId(), "订单已创建");
    }
}

// === 监听事件（Audit模块）===
@EventListener
public void onOrderEvent(OrderEvent event) {
    recordAudit("ORDER", event.getEventType(), detail);
}
```

**事件驱动的核心价值：Notification 和 Audit 完全不知道 Order/User 模块的存在。
新增监听者时，源模块代码零修改。**

## 项目结构

```
12-SpringModulith模块化单体进阶/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/modulith/advanced/
    │   │   ├── ModulithAdvancedApplication.java     # 启动类
    │   │   ├── shared/                              # 共享模块
    │   │   │   ├── dto/                             # 共享DTO
    │   │   │   ├── event/                           # 事件定义（DomainEvent基类）
    │   │   │   └── modularity/                      # ApplicationModule 注解
    │   │   └── modules/                             # 五个业务模块
    │   │       ├── user/                            # 用户模块（基础）
    │   │       │   ├── api/
    │   │       │   ├── service/internal/
    │   │       │   ├── controller/
    │   │       │   └── entity/
    │   │       ├── product/                         # 商品模块（基础）
    │   │       ├── order/                           # 订单模块（上游）
    │   │       ├── notification/                    # 通知模块（事件驱动）
    │   │       │   ├── service/internal/
    │   │       │   └── listener/
    │   │       └── audit/                           # 审计模块（事件驱动）
    │   │           ├── service/internal/
    │   │           └── listener/
    │   └── resources/
    │       ├── application.properties               # 端口8102
    │       ├── templates/
    │       └── static/css/style.css
    └── test/java/.../
        └── ModularityTest.java                      # 16项模块化验证测试
```

## 模块化验证测试（16项）

| 测试编号 | 验证内容 | 层次 |
|---------|---------|------|
| M1 | 五个模块目录全部存在 | 目录结构 |
| M2 | 业务模块包含api包和internal包 | 包结构 |
| M3 | 事件驱动模块有listener子包 | 包结构 |
| M4 | shared包独立，不反向依赖业务模块 | 依赖方向 |
| M5 | entity包不被其他模块直接引用 | 封装性 |
| M6 | service.internal包不被其他模块直接引用 | 封装性 |
| M7 | order通过api接口引用user/product（DIP） | 依赖规则 |
| M8 | user和product模块互不依赖 | 依赖规则 |
| M9 | notification不反向依赖order/user实现 | 事件解耦 |
| M10 | 模块依赖无循环（拓扑排序验证） | 循环依赖 |
| M11 | user模块是基础模块（无业务依赖） | 层次结构 |
| M12 | product模块是基础模块（无业务依赖） | 层次结构 |
| M13 | notification监听OrderEvent和UserEvent | 事件驱动 |
| M14 | audit监听OrderEvent和UserEvent | 事件驱动 |
| M15 | user模块发布UserEvent事件 | 事件发布 |
| M16 | order模块发布OrderEvent事件 | 事件发布 |

## 运行方式

```bash
cd "12-SpringModulith模块化单体进阶"
mvn spring-boot:run
```

### 访问地址
- 首页: http://localhost:8102
- 用户管理: http://localhost:8102/users
- 商品管理: http://localhost:8102/products
- 订单管理: http://localhost:8102/orders
- 通知中心: http://localhost:8102/notifications
- 审计日志: http://localhost:8102/audit

### 运行测试
```bash
mvn test -Dtest=ModularityTest
```

## 上手指南：体验事件驱动通信

1. 启动应用后访问 http://localhost:8102
2. 在「用户管理」创建一个用户
3. 在「订单管理」创建订单（选择用户和商品）
4. 点击「完成」或「取消」订单
5. 访问「通知中心」查看生成的通知记录
6. 访问「审计日志」查看所有操作的审计记录

**关键观察：** 整个过程中，Notification 和 Audit 模块没有依赖 Order/User 模块的任何代码，完全通过事件驱动自动响应。

## 如何添加新模块

### 方式1：事件驱动模块
1. 在 `modules/` 下创建目录 `newmodule/`
2. 创建 `service/internal/NewModuleServiceImpl.java`（业务逻辑）
3. 创建 `listener/NewModuleController.java`（Web展示）
4. 如果需要监听事件，使用 `@EventListener` 注解
5. 如果被监听的事件在 shared/event/ 中添加
6. 运行 ModularityTest 验证

### 方式2：API模块
1. 创建 `api/NewModule.java`（对外接口）
2. 创建 `service/internal/NewModuleServiceImpl.java`（实现）
3. 创建 `entity/` 包（内部数据）
4. 创建 `controller/` 包（Web层）
5. 如果依赖其他模块，通过构造器注入对应模块的 API 接口

## 与 Spring Modulith 的对应关系

| Spring Modulith 进阶特性 | 本项目实现方式 |
|------------------------|--------------|
| @ApplicationModule | 自定义注解描述模块元信息 |
| 事件驱动（Spring Modulith Events） | Spring ApplicationEvent + @EventListener |
| 模块封装（Opened/Packages） | api/ + internal/ 包结构约束 |
| 模块间 DIP | 构造器注入 + 接口隔离 |
| ModularityTest | 手写16项验证（类 ArchUnit） |
| 循环依赖检测 | 拓扑排序算法验证 |
| 审计事件 | @EventListener 记录所有事件 |

## 进阶版 vs 基础版对比

| 特性 | 基础版 | 进阶版 |
|------|--------|--------|
| 模块数量 | 3 | 5 |
| 事件驱动 | 仅UserEvent/OrderEvent发布 | + Notification/Audit监听 |
| API包 | api/ | api/ |
| 内部封装 | entity/ | service/internal/ entity/ |
| 验证测试 | 7项 | 16项 |
| 循环依赖检测 | 简单三节点 | 拓扑排序 |
| 前端页面 | 用户/商品/订单 | + 通知中心/审计日志 |
