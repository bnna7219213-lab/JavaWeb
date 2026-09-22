# 08 - 原生 ESM 微前端进阶

## 项目概述

完整的微前端架构实现，基于 Import Maps + 动态 `import()` + Event Bus + 生命周期管理。包含 Host App 壳、2个独立微前端子应用（用户管理、商品管理）、应用注册表、完整的事件总线和生命周期管理。

## 架构示意

```
┌─────────────────────────────────────────────────────────────────┐
│                       Host App 壳                                │
│                                                                  │
│  ┌───────────────────────────────────────────────────────────┐   │
│  │                  Import Map (共享 React)                    │   │
│  │   react -> esm.sh/react@18.3.1 (所有子应用共享同一份)       │   │
│  └───────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌──────────────────────┐       ┌──────────────────────────┐    │
│  │   App Registry       │       │  Lifecycle Manager        │    │
│  │  (应用注册表)          │       │  (生命周期管理)            │    │
│  │  /api/apps 动态发现   │       │  mount / unmount / reload │    │
│  └──────────────────────┘       └──────────────────────────┘    │
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │   import()  │  │   import()  │  │  Event Bus  │             │
│  │   UserMod   │  │  ProductMod │  │  (事件总线)  │             │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘             │
│         │                │                 │                     │
│  ┌──────▼──────┐  ┌──────▼──────┐                                  │
│  │ UserModule  │  │ ProductModule│  ← 独立 ES Module             │
│  │ 用户管理     │  │ 商品管理     │    独立开发/独立部署           │
│  └─────────────┘  └─────────────┘                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 核心机制

### 1. 应用注册表 (App Registry)
```javascript
// /api/apps - 服务端提供应用清单
[
  { name: "user-module", entry: "/microapp/UserModule/main.js", ... },
  { name: "product-module", entry: "/microapp/ProductModule/main.js", ... }
]

// Host App 加载时获取注册表，用户界面展示可管理的子应用
```

### 2. 生命周期管理
```javascript
// 1. 懒加载子应用
const module = await import('/microapp/UserModule/main.js');

// 2. 挂载 - 渲染到容器 + 注册事件监听
const instance = await module.mount(container, { apiBase: '/api' });

// 3. 使用 - 子应用独立运行

// 4. 卸载 - 清理资源 + 移除事件
module.unmount(instance);

// 5. 重载 (模拟重新部署)
await import('/microapp/UserModule/main.js?t=' + Date.now());
```

### 3. 子应用间通信 (Event Bus)
```javascript
// 子应用 A 发送
window.dispatchEvent(new CustomEvent('microapp-event', {
  detail: { source: 'UserModule', eventName: 'user:selected', data: user }
}));

// 子应用 B 监听
window.addEventListener('microapp-event', (e) => {
  if (e.detail.source === 'UserModule' && e.detail.eventName === 'user:selected') {
    // 联动逻辑
  }
});
```

### 4. 独立开发与部署
每个子应用是标准 ES Module，可：
- 独立开发（不同的目录、不同的团队维护）
- 独立部署（替换单文件即可热更新）
- 独立测试（可直接在浏览器单独加载测试）

## 技术栈

| 层级 | 技术 |
|------|------|
| JDK | 21 |
| Spring Boot | 3.3.4 (内嵌 Tomcat) |
| 后端 | Spring Web + REST API |
| 前端 | 原生 ES Module, React 18 (Import Maps) |
| 通信 | CustomEvent + Event Bus |
| 运行时 | 浏览器原生支持，零构建 |

## 端口

- **后端 API**: http://localhost:8094/api
- **前端页面**: http://localhost:8094/

## 起止方式

```bash
cd 08-原生ESM微前端进阶
mvn spring-boot:run
```

浏览器打开 http://localhost:8094/

点击各子应用容器的 **Mount** 按钮加载对应的微前端。

## API 接口

### 用户模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users | 用户列表 (支持 ?department=xxx 筛选) |
| GET | /api/users/{id} | 单个用户 |
| POST | /api/users | 创建用户 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |
| GET | /api/users/stats/departments | 部门统计 |

### 商品模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/products | 商品列表 (支持 ?category=xxx 筛选) |
| GET | /api/products/{id} | 单个商品 |
| POST | /api/products | 创建商品 |
| PUT | /api/products/{id} | 更新商品 |
| DELETE | /api/products/{id} | 删除商品 |
| GET | /api/products/stats/categories | 分类统计 |

### 应用注册
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/apps | 应用注册表 |
| GET | /api/apps/{name} | 单个应用配置 |

## 项目结构

```
08-原生ESM微前端进阶/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/esm/
    │   ├── EsmMicroFrontendAdvancedApplication.java
    │   ├── entity/
    │   │   ├── User.java
    │   │   └── Product.java
    │   ├── service/
    │   │   ├── UserService.java
    │   │   └── ProductService.java
    │   └── controller/
    │       ├── UserController.java
    │       ├── ProductController.java
    │       └── AppRegistryController.java
    └── resources/
        ├── application.properties              (端口: 8094)
        └── static/
            ├── index.html                       (Host App 壳 + 生命周期管理)
            ├── import-map.json                  (共享依赖配置)
            ├── css/style.css                    (全局样式)
            ├── shared/
            │   ├── event-bus.js                 (事件总线 - 子应用间通信)
            │   └── api-client.js                (共享 API 请求工具)
            └── microapp/
                ├── UserModule/
                │   └── main.js                   (用户管理子应用)
                └── ProductModule/
                    └── main.js                  (商品管理子应用)
```

## 运行效果

1. 浏览器访问 http://localhost:8094/
2. 看到两个应用容器（UserModule + ProductModule），状态为 "未加载"
3. 点击 **Mount** 按钮，Host App 动态 `import()` 下载并挂载子应用
4. 子应用渲染后以独立 React 应用形式运行，有自己的数据和 UI
5. 子应用之间的通信通过 CustomEvent 实现（如点击用户/商品触发联动）
6. 点击 **Unmount** 完整卸载子应用，清理所有资源
7. 点击 **Reload** 模拟子应用重新部署

## ESM 微前端核心价值

| 特性 | 说明 |
|------|------|
| 零构建 | 无需 Webpack/Vite/Rollup，浏览器原生 ESM |
| 独立开发 | 每个子应用独立 ES Module，不同团队可并行 |
| 独立部署 | 单文件替换即可热更新，无需重新构建整个应用 |
| 依赖去重 | Import Maps 保证 React 等共享库只加载一次 |
| 懒加载 | 只在用户访问时才下载对应子应用的代码 |
| 松耦合 | 子应用间通过事件通信，互不依赖 |
| 标准化 | 基于 W3C Import Maps + ESM 标准 |

## 浏览器要求

- Chrome 89+ / Edge 89+ (原生支持 Import Maps)
- Safari 16.4+
- Firefox 108+
