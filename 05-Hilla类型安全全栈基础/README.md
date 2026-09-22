# 05 - Hilla 类型安全全栈基础

## 架构说明

Hilla（原名 Vaadin Fusion）是一个类型安全的全栈 Web 框架，核心思想是：

> **"定义一次 Java 类型，前后端自动共享，消除手动维护 API 类型的开销和错误。"**

Hilla 通过在编译期扫描 Java `@Endpoint` 类，自动生成对应的 TypeScript 客户端代码（`.d.ts` 类型定义 + API 调用封装文件），使得前端调用后端 API 时获得完整的类型提示和编译时类型检查。

**本项目以模拟方案展示同样的架构效果**：使用 `@RestController` 代替 `@Endpoint`，预先生成的 TypeScript 客户端文件模拟 Hilla 的自动生成产物。

## 技术栈

| 层 | 技术 |
|-----|------|
| 后端 | Spring Boot 3.3 + Maven |
| Java 反向模拟 Hilla | `@RestController` + `@RequestMapping` |
| 前端 | 原生 TypeScript + HTML（零构建） |
| 自动生成模拟 | `frontend/models.d.ts` + `frontend/endpoints.ts` |
| Java 版本 | 21 |

## 项目结构

```
05-Hilla类型安全全栈基础/
├── pom.xml                            # Maven 配置（Spring Boot 3.3 Starter Web）
├── README.md                          # 本文件
├── frontend/                          # 源码层（Hilla 风格的 TypeScript 源文件）
│   ├── models.d.ts                    # 【模拟Hilla自动生成】实体类型定义 .d.ts
│   ├── endpoints.ts                   # 【模拟Hilla自动生成】API 客户端封装文件
│   └── app.ts                         # 前端应用逻辑（使用上述生成文件）
└── src/main/
    ├── java/com/example/hilla/
    │   ├── HillaTypeSafeBasicApplication.java  # 启动类
    │   ├── entity/User.java                    # 用户实体
    │   ├── endpoint/UserEndpoint.java          # 【模拟@Endpoint】用户端点
    │   └── config/CorsConfig.java              # 跨域配置
    └── resources/
        ├── application.properties              # 端口配置
        └── static/                             # 静态资源（编译后的前端）
            ├── index.html                      # 主页面
            └── frontend/
                ├── models.js                   # models.d.ts 编译产物
                ├── endpoints.js                # endpoints.ts 编译产物
                └── app.js                      # app.ts 编译产物
```

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user/list` | 获取所有用户列表 |
| GET | `/api/user/{id}` | 根据 ID 获取单个用户 |
| POST | `/api/user/add` | 新增用户 |

## 运行方式

1. 右键 `HillaTypeSafeBasicApplication.java` → Run As → Java Application
2. 访问 **http://localhost:8085/**

## 接口调用

```bash
# 获取用户列表
curl http://localhost:8085/api/user/list

# 获取单个用户
curl http://localhost:8085/api/user/1

# 新增用户
curl -X POST http://localhost:8085/api/user/add \
  -H "Content-Type: application/json" \
  -d '{"name":"赵六","age":25,"email":"zhaoliu@example.com"}'
```

## Hilla 架构原理

### 核心工作流

```
┌─────────────────────────────────────────────────────┐
│                    Hilla 编译流程                      │
│                                                      │
│  1. Java @Endpoint 类                                 │
│         │                                            │
│         ▼                                            │
│  2. Hilla Maven Plugin 扫描分析                        │
│         │                                            │
│         ▼                                            │
│  3. 自动生成 TypeScript 代码                           │
│         ├─ frontend/generated/models.d.ts  (类型定义)  │
│         └─ frontend/generated/endpoints.ts  (API客户端) │
│         │                                            │
│         ▼                                            │
│  4. 前端 TypeScript 直接 import 调用                    │
│         │                                            │
│         ▼                                            │
│  5. 编译时发现类型错误 → 提前暴露 API 变更问题             │
└─────────────────────────────────────────────────────┘
```

### 对比传统前后端分离开发

| 传统方式 | Hilla 方式 |
|---------|-----------|
| 后端改了 API，前端可能不知道 | 后端改了 Endpoint，前端类型报错 |
| 手写 API 调用，容易拼错路径 | 自动生成路径和参数类型 |
| 接口文档可能过时 | 代码即文档，永远实时同步 |
| 前后端联调时间长 | 编译器帮你检测不匹配 |
| `fetch('/api/user/{id}')` | `UserEndpoint.getUserById(id)` 有类型提示 |

### 类型共享机制示例

```java
// --- 后端 Java 实体 ---
public class User {
    private Integer id;
    private String name;
    private Integer age;
    private String email;
}
```

```typescript
// --- 对应自动生成的 TypeScript 接口 ---
export interface User {
    id: number;        // Integer → number
    name: string;      // String → string
    age: number;       // Integer → number
    email: string | null;  // 可能为 null 的字段
}
```

### Hilla 关键注解（本项目用 @RestController 模拟）

| Hilla 注解 | 本项目模拟方式 | 说明 |
|-----------|-------------|------|
| `@Endpoint` | `@RestController` | 标记端点类 |
| `@EndpointExposed` | （隐含） | 暴露给前端的类 |
| `@AnonymousAllowed` | 无鉴权 | 允许匿名访问 |
| `@PermitAll` | 无鉴权 | 允许所有角色访问 |

## 数据模型

### User 实体

| 字段 | Java 类型 | TS 类型 | 说明 |
|------|----------|---------|------|
| id | Integer | number | 用户唯一标识 |
| name | String | string | 用户姓名 |
| age | Integer | number | 年龄 |
| email | String | string \| null | 邮箱（可空） |
