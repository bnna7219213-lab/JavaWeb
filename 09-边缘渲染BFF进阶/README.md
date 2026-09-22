# 09-边缘渲染BFF进阶版

## 项目概述

完整实现**BFF+边缘渲染分层架构**，包含多端适配（PC/Mobile）、服务端渲染（SSR）、边缘CDN缓存模拟等企业级特性。

## 完整分层架构

```
客户端层          BFF边缘层                     缓存层              内部服务层
─────────       ──────────────               ─────────           ──────────────

PC Web  ←──→  PcBffController          ┌──────────────┐
                 ├─ @Cacheable           │ dashboard    │    ┌──────────────┐
                 ├─ 并行聚合             │ user-info    │←───│ UserService   │
                 └─ 完整6个推荐          │ order-detail │    └──────────────┘
                                         │ product-list │    ┌──────────────┐
H5移动  ←──→  MobileBffController       └──────────────┘←───│ OrderService  │
                 ├─ @Cacheable                 ↑             └──────────────┘
                 ├─ 并行聚合               @CacheEvict    ┌──────────────┐
                 └─ 精简3个推荐            (失效策略) ←───│RecommendSvc  │
                                                          └──────────────┘
SSR渲染 ←──→  SsrController
                 ├─ 服务端渲染HTML片段
                 └─ 客户端直接展示（无白屏）
```

## 核心特性

### 1. 多端BFF端点

| 端类型 | Controller | 路由前缀 | 数据特点 |
|--------|-----------|---------|---------|
| PC Web | `PcBffController` | `/api/pc/*` | 6个推荐商品、完整用户信息、大屏适配 |
| H5移动 | `MobileBffController` | `/api/mobile/*` | 3个推荐商品、精简字段、快捷入口 |
| SSR渲染 | `SsrController` | `/api/ssr/*` | 服务端输出HTML片段，客户端直接展示 |

### 2. 边缘CDN缓存模拟

使用Spring Cache在BFF层构建缓存，模拟真实CDN边缘节点的缓存行为：

```java
// 缓存命中时直接返回，不执行方法体
@Cacheable(value = "dashboard", key = "'pc-dashboard'")
public DashboardVO getPcDashboard() { ... }

// 数据变更时清除缓存
@CacheEvict(value = "dashboard", allEntries = true)
public void evictDashboardCache() { ... }
```

### 3. 服务端渲染（SSR）

BFF层直接渲染HTML片段返回，消除首屏白屏时间：

```
对比：
  CSR（客户端渲染）: 空HTML → 下载JS → JS请求数据 → JS渲染 → 看到内容
  SSR（服务端渲染）: 完整HTML → 立即看到内容 → 后台加载JS增强交互
```

## 目录结构

```
09-边缘渲染BFF进阶/
├── pom.xml                                          # Maven配置 (Spring Boot 3.3.5, JDK 21, Cache)
├── src/main/java/com/aether/bff/
│   ├── BffAdvancedApplication.java                  # 启动类（@EnableCaching）
│   ├── controller/
│   │   ├── PcBffController.java                     # PC Web端BFF端点
│   │   ├── MobileBffController.java                 # H5移动端BFF端点
│   │   └── SsrController.java                       # SSR服务端渲染端点
│   ├── service/
│   │   ├── UserService.java                         # 用户服务（@Cacheable缓存）
│   │   ├── OrderService.java                        # 订单服务
│   │   ├── RecommendService.java                    # 推荐服务（PC/移动端分方法）
│   │   └── DashboardService.java                    # 聚合服务（@Cacheable + 并行）
│   ├── config/
│   │   └── CacheConfig.java                         # 缓存配置（4个缓存空间）
│   └── entity/
│       ├── User.java                                # 用户实体
│       ├── Product.java                             # 商品实体
│       ├── Order.java                               # 订单实体
│       └── DashboardVO.java                         # 聚合视图（含QuickAction）
├── src/main/resources/
│   ├── application.properties                       # 端口8096
│   └── static/index.html                            # 多端切换前端页面
└── README.md
```

## 接口说明

| 接口 | 方法 | 设备端 | 说明 |
|------|------|--------|------|
| `/api/pc/dashboard` | GET | PC | PC端完整Dashboard（6个推荐） |
| `/api/pc/user-card` | GET | PC | PC端用户卡片 |
| `/api/pc/ping` | GET | PC | 状态检查 |
| `/api/mobile/dashboard` | GET | 移动端 | 精简Dashboard（3个推荐+快捷入口） |
| `/api/mobile/home-feed` | GET | 移动端 | 首页信息流数据 |
| `/api/mobile/ping` | GET | 移动端 | 状态检查 |
| `/api/ssr/render/home` | GET | SSR | 服务端渲染首页HTML片段 |
| `/api/ssr/render/user-card` | GET | SSR | 服务端渲染用户卡片HTML片段 |
| `/index.html` | GET | - | 前端演示页面 |

## 缓存策略详解

### 缓存空间设计

| 缓存空间 | 缓存键 | 数据内容 | 失效策略 |
|---------|--------|---------|---------|
| `dashboard` | `pc-dashboard` / `mobile-dashboard` | 完整Dashboard | TTL过期 + Evict手动清除 |
| `user-info` | `current-user` / `{userId}` | 用户信息 | @CacheEvict清除 |
| `order-detail` | `recent-orders` | 近期订单 | 订单事件触发失效 |
| `product-list` | `recommend-pc` / `recommend-mobile` | 推荐商品 | TTL过期 |

### 缓存命中效果

```
首次请求: @Cacheable未命中 → 并行调3个服务 ~200ms → 组装 → 缓存 → 返回
二次请求: @Cacheable命中   → 直接返回缓存值 ~1ms

加速比: ~200x（200ms vs 1ms）
```

### 缓存失效策略

1. **TTL自动过期**：缓存到达设定时间后自动失效
2. **@CacheEvict主动清除**：数据变更时调用evict方法
3. **事件驱动**：监听业务事件（下单、收藏等）触发失效

## 多端适配策略

BFF层根据设备类型返回差异化数据，前端无需关心设备判断逻辑：

```
PC端请求 → PcBffController → DashboardService.getPcDashboard()
                                 ├─ RecommendService.forPc()     6个推荐
                                 └─ 完整用户信息（含bio等）

移动请求 → MobileBffController → DashboardService.getMobileDashboard()
                                   ├─ RecommendService.forMobile()  3个推荐
                                   ├─ 精简字段（节省流量）
                                   └─ quickActions 快捷入口
```

## 运行方式

```bash
# 编译运行
mvn spring-boot:run

# 或打包运行
mvn clean package
java -jar target/09-edge-bff-advanced-1.0.0.jar
```

启动后访问：
- 前端演示页面：http://localhost:8096/index.html
- PC端BFF API：http://localhost:8096/api/pc/dashboard
- 移动端BFF API：http://localhost:8096/api/mobile/dashboard
- SSR HTML片段：http://localhost:8096/api/ssr/render/home

## BFF架构进阶要点

### 1. 边缘渲染分层
CDN缓存层位于BFF层之后、内部服务层之前。真实架构中：
- CDN（CloudFlare等）缓存静态资源和部分API响应
- BFF层做数据聚合 + 细粒度缓存（用户维度）
- 内部服务处理核心业务逻辑

### 2. 并行聚合优化
使用`CompletableFuture`并行调用内部服务：
- 串行: user(80ms) + order(120ms) + recommend(200ms) = ~400ms
- 并行: max(80ms, 120ms, 200ms) = ~200ms

### 3. SSR与CSR的混合策略
现代BFF最佳实践是混合渲染：
- 首屏关键内容使用SSR（直接输出HTML）
- 非关键交互区域使用CSR（客户端异步请求）
- 两者共享同一个BFF数据聚合层

### 4. 生产环境扩展建议
- 缓存：替换`ConcurrentMapCacheManager`为**Redis**（支持分布式TTL）
- 并行：使用**WebFlux**（WebClient异步HTTP）替代阻塞式CompletableFuture
- 失效：引入**消息队列**（Kafka/RabbitMQ）实现事件驱动缓存失效
- 容错：结合**Resilience4j**实现降级和熔断
