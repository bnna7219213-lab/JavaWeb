# 09-边缘渲染BFF基础版

## 项目概述

模拟**边缘层BFF（Backend For Frontend）**架构模式，演示服务端数据聚合的核心概念。

## 架构分层

```
┌─────────────────────────────────────────────────────┐
│  前端SPA (index.html)                                │
│  - 纯客户端渲染                                      │
│  - 只需1次HTTP请求即可获得完整页面数据               │
└───────────────────────┬─────────────────────────────┘
                        │ GET /api/bff/dashboard
                        v
┌─────────────────────────────────────────────────────┐
│  BFF层 (Controller + Service)                       │
│  - 为前端定制的聚合API                               │
│  - 内部调用多个微服务，聚合成前端需要的格式          │
└───────┬──────────────┬──────────────┬───────────────┘
        │              │              │
        v              v              v
┌──────────┐  ┌──────────────┐  ┌────────────────┐
│用户服务   │  │订单服务       │  │推荐服务         │
│(模拟)     │  │(模拟)         │  │(模拟)           │
│~80ms      │  │~120ms         │  │~200ms           │
└──────────┘  └──────────────┘  └────────────────┘
```

## BFF核心价值

| 对比项 | 传统方式（前端直调微服务） | BFF方式 |
|--------|--------------------------|---------|
| 前端请求数 | 3-4次（每个服务各调一次） | 1次（聚合接口） |
| 网络开销 | 多次HTTP握手往返 | 1次请求 |
| 数据适配 | 前端做字段筛选/格式化 | BFF层已完成裁剪 |
| 服务地址 | 前端知道所有微服务地址 | 只知道BFF地址 |
| 错误处理 | 前端处理各服务异常 | BFF统一处理降级 |

## 目录结构

```
09-边缘渲染BFF基础/
├── pom.xml                                          # Maven配置 (Spring Boot 3.3.5, JDK 21)
├── src/main/java/com/aether/bff/
│   ├── BffBasicApplication.java                     # 启动类
│   ├── controller/
│   │   └── BffController.java                       # BFF端点（聚合API）
│   ├── service/
│   │   ├── UserService.java                         # 模拟用户微服务
│   │   ├── OrderService.java                        # 模拟订单微服务
│   │   ├── RecommendService.java                    # 模拟推荐微服务
│   │   └── DashboardService.java                    # BFF聚合服务（核心）
│   └── entity/
│       ├── User.java                                # 用户实体
│       ├── Product.java                             # 商品实体
│       ├── Order.java                               # 订单实体（含OrderItem）
│       └── DashboardVO.java                         # 聚合视图对象
├── src/main/resources/
│   ├── application.properties                       # 端口8095
│   └── static/index.html                            # 前端SPA页面
└── README.md
```

## 接口说明

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/bff/dashboard` | GET | 聚合Dashboard数据（串行调用三个服务） |
| `/api/bff/dashboard/parallel` | GET | 并行聚合（CompletableFuture优化） |
| `/api/bff/user-card` | GET | 轻量聚合（仅用户+订单数） |
| `/api/bff/ping` | GET | BFF服务状态检查 |
| `/index.html` | GET | 前端SPA页面 |

## DashboardVO 聚合结构

```json
{
  "success": true,
  "data": {
    "pageTitle": "我的首页",
    "user": { "id": "U-10086", "username": "张三", "memberLevel": "VIP-金卡", ... },
    "recentOrders": [ { "orderId": "ORD-...", "statusText": "已发货", ... } ],
    "recommendations": [ { "name": "智能手表Pro", "price": 1999.00, ... } ],
    "unreadNotificationCount": 3
  },
  "bffLayer": "basic",
  "aggregationTimeMs": 402
}
```

## 运行方式

```bash
# 编译运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/09-edge-bff-basic-1.0.0.jar
```

启动后访问：
- 前端页面：http://localhost:8095/index.html
- BFF API：http://localhost:8095/api/bff/dashboard

## BFF架构原理

### 1. Backend For Frontend 模式
BFF是**为特定前端定制的后端层**。不同于通用的REST API，BFF接口的返回结构就是前端页面组件直接需要的形状，零额外转换。

### 2. 数据聚合减少请求
传统方式下，前端渲染一个Dashboard需要：
1. `GET /api/users/me` → 用户信息
2. `GET /api/orders?limit=5` → 订单列表
3. `GET /api/recommendations` → 推荐商品

BFF方式下只需：
1. `GET /api/bff/dashboard` → 一次拿到全部

### 3. 后端并行调用优化
进阶模式使用`CompletableFuture`并行调用内部服务，将总耗时从串行累加变为最慢服务的耗时：
- 串行：80ms + 120ms + 200ms ≈ 400ms
- 并行：max(80, 120, 200) ≈ 200ms

### 4. 领域边界隔离
前端不直接知道底层微服务的存在，所有服务调用细节封装在BFF层内部。当内部服务变更时，只需修改BFF层适配，前端无感。
