# Spring Modulith 模块化单体 - 基础版

## 项目概述

本项目演示了如何使用 Spring Boot 3.3 + JDK 21 构建模块化单体应用（Modular Monolith）。

核心思想：**在一个 Spring Boot 应用中，通过清晰的包结构和接口隔离，模拟 Spring Modulith 的模块化设计模式，实现模块间的解耦。**

## 技术栈

- JDK 21
- Spring Boot 3.3.5
- Spring Web (端口: 8101)
- Thymeleaf 模板引擎
- Lombok

## 模块化设计原则

### 1. 模块封装（Module Encapsulation）
每个模块是一个独立的包，有明确的边界：

| 模块 | 包路径 | 职责 |
|------|--------|------|
| user | `com.example.modulith.basic.modules.user` | 用户管理 |
| product | `com.example.modulith.basic.modules.product` | 商品管理 |
| order | `com.example.modulith.basic.modules.order` | 订单管理（跨模块） |
| shared | `com.example.modulith.basic.shared` | 共享DTO和事件 |

### 2. 接口隔离（Interface Isolation）
每个模块通过 `api` 子包暴露对外接口，其他模块只能通过此接口访问：

```
modules/user/
├── api/          ← 对外API（UserModule接口）
├── service/      ← 内部实现（UserService）
├── controller/   ← Web控制层
└── entity/       ← 内部数据结构（UserEntity）

modules/order/
├── api/          ← 对外API（OrderModule接口）
├── service/      ← 内部实现（OrderService引用UserModule/ProductModule接口）
├── controller/   ← Web控制层
└── entity/       ← 内部数据结构（OrderEntity）
```

### 3. 依赖倒置（Dependency Inversion）
OrderService 依赖 UserModule/ProductModule 接口，而非具体实现类：

```java
@Service
public class OrderService implements OrderModule {
    private final UserModule userModule;      // 依赖接口
    private final ProductModule productModule; // 依赖接口
    
    public OrderService(UserModule userModule, ProductModule productModule, ...) {
        this.userModule = userModule;
        this.productModule = productModule;
    }
}
```

### 4. 事件驱动通信（Event-Driven Communication）
模块通过 Spring ApplicationEvent 进行松耦合通信：

```java
// 订单模块发布事件
eventPublisher.publishEvent(OrderEvent.created(...));

// 用户模块停用时发布事件
eventPublisher.publishEvent(UserEvent.deactivated(...));
```

## 模块依赖关系

```
         Application
        /     |      \
   User    Product   Order
   Module  Module    Module
      \       |      /
       \      |     /
        \     |    /
     通过接口 + 事件驱动通信

Order Module
  ├── 依赖 UserModule 接口
  ├── 依赖 ProductModule 接口
  └── 发布 OrderEvent 事件

User Module: 独立模块（不依赖其他业务模块）
Product Module: 独立模块（不依赖其他业务模块）
```

**关键约束：**
- user 和 product 模块之间没有直接依赖
- order 模块通过接口引用 user/product，不引用它们的 Entity 或内部实现
- shared 包不包含业务逻辑，只提供 DTO 和事件定义

## 项目结构

```
12-SpringModulith模块化单体基础/
├── pom.xml                          # Maven 依赖配置
├── README.md                        # 本文档
└── src/
    ├── main/
    │   ├── java/com/example/modulith/basic/
    │   │   ├── ModulithBasicApplication.java    # 启动类
    │   │   ├── shared/                          # 共享模块
    │   │   │   ├── dto/                         # 跨模块DTO
    │   │   │   │   ├── UserDTO.java
    │   │   │   │   ├── ProductDTO.java
    │   │   │   │   └── OrderDTO.java
    │   │   │   └── event/                       # 领域事件
    │   │   │       ├── DomainEvent.java
    │   │   │       ├── UserEvent.java
    │   │   │       └── OrderEvent.java
    │   │   └── modules/                         # 业务模块
    │   │       ├── user/                        # 用户模块
    │   │       │   ├── api/UserModule.java      # 对外API接口
    │   │       │   ├── service/UserService.java # 服务实现
    │   │       │   ├── controller/
    │   │       │   └── entity/
    │   │       ├── product/                     # 商品模块
    │   │       │   ├── api/ProductModule.java
    │   │       │   ├── service/ProductService.java
    │   │       │   ├── controller/
    │   │       │   └── entity/
    │   │       └── order/                       # 订单模块（跨模块）
    │   │           ├── api/OrderModule.java
    │   │           ├── service/OrderService.java # 引用UserModule/ProductModule接口
    │   │           ├── controller/
    │   │           └── entity/
    │   └── resources/
    │       ├── application.properties           # 端口8101
    │       ├── templates/                       # Thymeleaf模板
    │       │   ├── index.html
    │       │   ├── user/users.html
    │       │   ├── product/products.html
    │       │   └── order/orders.html, order-create.html
    │       └── static/css/style.css
    └── test/java/.../
        └── ModularityTest.java                  # 模块化架构验证测试
```

## 如何添加新模块

1. 在 `modules/` 下创建新模块目录：`modules/{module-name}/`
2. 创建子包：`api/`, `service/`, `controller/`, `entity/`
3. 在 `api/` 下定义模块接口（如 `PaymentModule`）
4. 在 `service/` 下实现接口
5. 如果需要引用其他模块，通过构造器注入对应模块的 API 接口：
   ```java
   public PaymentService(OrderModule orderModule, UserModule userModule) { ... }
   ```
6. 添加对应的 Controller 和前端模板
7. 运行 `ModularityTest` 验证架构未被破坏

## 运行方式

### 方式1：Maven 命令行
```bash
cd "12-SpringModulith模块化单体基础"
mvn spring-boot:run
```

### 方式2：IDE
运行 `ModulithBasicApplication.main()`

### 访问地址
- 首页: http://localhost:8101
- 用户管理: http://localhost:8101/users
- 商品管理: http://localhost:8101/products
- 订单管理: http://localhost:8101/orders

## 模块化验证测试

`ModularityTest.java` 包含7个验证规则：

| 测试编号 | 验证内容 |
|---------|---------|
| M1 | 所有模块目录存在 |
| M2 | 每个模块包含 api/ 子包和接口文件 |
| M3 | entity 包不被其他模块直接引用 |
| M4 | 跨模块引用必须通过 api 包接口 |
| M5 | shared 包不反向依赖业务模块 |
| M6 | user 和 product 模块相互独立 |
| M7 | 无循环依赖（依赖图无环） |

运行测试：
```bash
mvn test -Dtest=ModularityTest
```

## 与 Spring Modulith 的对应关系

| Spring Modulith 特性 | 本项目实现方式 |
|---------------------|--------------|
| ApplicationModule | 通过 api 包暴露模块接口 |
| 模块封装 | 通过包结构和命名约定强制隔离 |
| 模块事件 | Spring ApplicationEvent |
| 模块间 DIP | 构造器注入接口 |
| ArchUnit 验证 | 手写 Java 测试（ModularityTest） |
| spring-data-repository | 模拟内存存储（可替换为 JPA） |

## 设计优势

1. **清晰边界**：每模块独立，新开发者快速理解系统
2. **可测试**：接口 mock 后每模块可独立单元测试
3. **可演进**：模块可抽取为独立微服务（边界已定义好）
4. **防退化**：ModularityTest 阻止结构腐化
5. **团队并行**：不同团队负责不同模块，接口即契约
