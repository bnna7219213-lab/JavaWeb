# 05 - Hilla 类型安全全栈进阶

## 架构说明

Hilla（原 Vaadin Fusion）是 Spring Boot + TypeScript 的类型安全全栈框架。

> **核心理念：一次定义，全栈共享。Java 后端类型安全地延伸到 TypeScript 前端，消除 API 类型不一致的隐患。**

**本项目以模拟方案展示同样的架构**：用 `@RestController` 模拟 `@Endpoint`，预生成的 TypeScript 文件模拟 Hilla 编译期自动生成。

相比基础版的提升：

| 特性 | 基础版 | 进阶版 |
|------|--------|--------|
| 端点数量 | 1个 (User) | 2个 (User + Order) |
| 服务层 | 无 | UserService + OrderService |
| CRUD 完整度 | 部分 (list/get/add) | 完整 (GET/POST/PUT/DELETE/PATCH) |
| 跨域 | 允许全部 | 允许全部 |
| 异常处理 | 无 | 全局异常处理器 + 统一 JSON 错误 |
| 表单验证 | 前端简单校验 | 类型安全验证模块 (validation.ts) |
| TypeScript 类型 | 基础 interface | 完整 interface + type + 错误类型 + 枚举 |

## 技术栈

| 层 | 技术 |
|-----|------|
| 后端 | Spring Boot 3.3 + Maven |
| Java 反向模拟 Hilla | `@RestController` + Service 层分离 |
| 前端 | TypeScript (原生 ESM) + HTML |
| 自动生成模拟 | frontend/*.d.ts + frontend/*.ts（源码层）|
| 异常处理 | @RestControllerAdvice + 统一 JSON |
| Java 版本 | 21 |

## 项目结构

```
05-Hilla类型安全全栈进阶/
├── pom.xml
├── README.md
├── frontend/                              # TypeScript 源码层
│   ├── models.d.ts                        # 实体类型定义 (模拟自动生成 .d.ts)
│   ├── endpoints.ts                       # API 客户端封装 (模拟自动生成)
│   ├── validation.ts                      # 类型安全表单验证
│   └── app.ts                             # 前端应用入口
└── src/main/
    ├── java/com/example/hilla/
    │   ├── HillaTypeSafeAdvancedApplication.java
    │   ├── entity/User.java, Order.java
    │   ├── service/UserService.java, OrderService.java
    │   ├── endpoint/UserEndpoint.java, OrderEndpoint.java
    │   ├── exception/GlobalExceptionHandler.java
    │   └── config/CorsConfig.java
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html                  # 主页面
            └── frontend/
                ├── models.js, endpoints.js, validation.js, app.js  # 编译产出
```

## API 接口

### User 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user/list` | 获取所有用户 |
| GET | `/api/user/{id}` | 根据 ID 查询用户 |
| POST | `/api/user` | 新增用户 |
| PUT | `/api/user/{id}` | 更新用户 |
| DELETE | `/api/user/{id}` | 删除用户 |
| GET | `/api/user/count` | 获取用户总数 |

### Order 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/order/list` | 获取所有订单 |
| GET | `/api/order/{id}` | 根据 ID 查询订单 |
| GET | `/api/order/user/{userId}` | 根据用户 ID 查询订单 |
| POST | `/api/order` | 创建订单 |
| PUT | `/api/order/{id}` | 更新订单 |
| PATCH | `/api/order/{id}/status` | 更新订单状态 |
| DELETE | `/api/order/{id}` | 删除订单 |

## 运行方式

1. 右键 `HillaTypeSafeAdvancedApplication.java` → Run As → Java Application
2. 访问 **http://localhost:8086/**

## 接口调用示例

```bash
# === 用户 CRUD ===
# 查询全部
curl http://localhost:8086/api/user/list

# 查询单个
curl http://localhost:8086/api/user/1

# 创建
curl -X POST http://localhost:8086/api/user \
  -H "Content-Type: application/json" \
  -d '{"name":"赵六","age":25,"email":"zhaoliu@example.com"}'

# 更新
curl -X PUT http://localhost:8086/api/user/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"张三-update","age":26,"email":"zs@new.com"}'

# 删除
curl -X DELETE http://localhost:8086/api/user/3


# === 订单 CRUD ===
# 查询全部订单
curl http://localhost:8086/api/order/list

# 根据用户查订单
curl http://localhost:8086/api/order/user/1

# 创建订单
curl -X POST http://localhost:8086/api/order \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productName":"iPad Pro","quantity":1,"price":6999.00,"status":"PENDING"}'

# 更新订单状态
curl -X PATCH http://localhost:8086/api/order/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"SHIPPED"}'

# 删除订单
curl -X DELETE http://localhost:8086/api/order/2
```

## Hilla 架构原理（进阶版重点）

### 1. 类型同步流水线

```
┌──────────────────────────────────────────────────────────────┐
│                    Hilla 类型安全同步流程                        │
│                                                               │
│   Java 实体类 ──→ Hilla 注解处理器 ──→ 自动生成 TS 文件          │
│                                                               │
│   com.example.entity.User                                     │
│       │                                                       │
│       └── frontend/generated/models.d.ts                     │
│           export interface User {                             │
│               id: number;      ← Integer                      │
│               name: string;    ← String                      │
│               age: number;     ← Integer                      │
│               email: string | null; ← Nullable               │
│           }                                                   │
│                                                               │
│   Java @Endpoint 类 ──→ Hilla 扫描 ──→ 生成 API 客户端         │
│                                                               │
│   UserEndpoint.listUsers(): List<User>                        │
│       │                                                       │
│       └── UserEndpoint.listUsers(): Promise<User[]>           │
│                                                               │
│   前端使用:                                                    │
│       const users = await UserEndpoint.listUsers();            │
│       // IDE 自动提示: users[0].name (string)                 │
│       // 编译时检查: 拼写错误在构建期暴露                         │
└──────────────────────────────────────────────────────────────┘
```

### 2. Bean Validation 联动

在真正的 Hilla 中，验证器会随类型自动同步到前端：

```java
// 后端 JPA 实体 + Bean Validation
public class User {
    @NotBlank @Size(min=1, max=50)
    private String name;

    @NotNull @Min(0) @Max(150)
    private Integer age;

    @Email
    private String email;
}
```

```typescript
// 前端 Hilla 自动生成的前端校验器
// (本项目 validation.ts 模拟此效果)
function validateUserForm(data): ValidationErrors {
    // name: 必填, 长度 1-50
    // age: 必填, 0-150
    // email: 选填, 格式校验
}
```

### 3. 错误处理机制

```
后端 GlobalExceptionHandler
    │
    ├── RuntimeException → 400 { status, message, timestamp }
    ├── NoSuchElement   → 404 { status, message, timestamp }
    └── Exception       → 500 { status, message, timestamp }
            │
            ▼
前端 handleResponse()
    │
    ├── response.ok === true  → return data (自动推断类型)
    └── response.ok === false → throw ApiException(status, message)
            │
            ▼
前端 try/catch
    catch (e) {
        if (e instanceof ApiException) {
            // TypeScript 知道 e.status, e.message, e.timestamp
        }
    }
```

### 4. 与基础版的关键差异

| 对比项 | 基础版 | 进阶版 |
|--------|--------|--------|
| 服务层 | 直接塞在 Endpoint | Endpoint + Service 分离 |
| 错误处理 | try/catch 直接抛 | 全局异常处理 + ApiException |
| 跨实体关联 | 无 | Order→User 关联查询 |
| PATCH 方法 | 无 | 订单状态单独更新 |
| 验证逻辑 | app.ts 中内联 | 独立 validation.ts 模块 |
| 类型定义 | 仅 interface | interface + type + generic |

## 数据模型

### User 实体

| 字段 | Java 类型 | TS 类型 | 约束 |
|------|----------|---------|------|
| id | Integer | number | 自增 |
| name | String | string | 1-50 字符, 必填 |
| age | Integer | number | 0-150, 必填 |
| email | String | string \| null | 格式校验, 选填 |

### Order 实体

| 字段 | Java 类型 | TS 类型 | 说明 |
|------|----------|---------|------|
| id | Long | number | 自增 |
| userId | Integer | number | 关联用户 ID |
| productName | String | string | 产品名称 |
| quantity | Integer | number | 数量 ≥ 1 |
| price | BigDecimal | number | 单价 > 0 |
| status | String (enum) | OrderStatus | 订单状态 |
| createdAt | String | string | 创建时间 |
