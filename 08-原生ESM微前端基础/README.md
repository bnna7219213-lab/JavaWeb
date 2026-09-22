# 08 - 原生 ESM 微前端基础

## 项目概述

基于 Import Maps + 动态 `import()` 实现的最简原生 ESM 微前端架构，无需任何构建工具，纯浏览器原生 ES Module 支持。

## 架构示意

```
┌─────────────────────────────────────────────────────┐
│                    Host App 壳                       │
│  (index.html + import-map + 动态加载逻辑)             │
│                                                      │
│  ┌─────────────────────────────────────────────┐    │
│  │        Import Map (共享依赖声明)              │    │
│  │  react → https://esm.sh/react@18.3.1         │    │
│  │  react-dom → https://esm.sh/react-dom@18.3.1 │    │
│  └─────────────────────────────────────────────┘    │
│                          │                           │
│              import('/microapp/app1/main.js')        │
│                          │                           │
│  ┌───────────────────────▼───────────────────────┐  │
│  │         MicroApp App1 (用户管理)               │  │
│  │    export function mount(container, opts)     │  │
│  │    export function unmount()                  │  │
│  │                                               │  │
│  │    window.dispatchEvent(                      │  │
│  │      new CustomEvent('microapp-event')        │  │
│  │    ) → 通知 Host                              │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

## 核心模式

### 1. Import Maps - 集中管理依赖

```html
<script type="importmap">
{
  "imports": {
    "react": "https://esm.sh/react@18.3.1",
    "react-dom": "https://esm.sh/react-dom@18.3.1"
  }
}
</script>
```

### 2. 动态 import() - 懒加载子应用

```javascript
// Host App
const app1Module = await import('/microapp/app1/main.js');
const instance = await app1Module.mount(container, options);
// 卸载
app1Module.unmount(instance);
```

### 3. 生命周期协议

```javascript
// 子应用必须导出
export async function mount(container, opts) { /* 渲染到 container */ }
export function unmount() { /* 清理资源 */ }
```

### 4. 主子通信 - CustomEvent

子应用通过 `window.dispatchEvent(new CustomEvent('microapp-event', { detail: ... }))` 向 Host 发送数据。

## 技术栈

| 层级 | 技术 |
|------|------|
| JDK | 21 |
| Spring Boot | 3.3.4 (内嵌 Tomcat) |
| 后端 | Spring Web (无额外框架) |
| 前端 | 原生 ES Module, React 18 (通过 CDN) |
| 依赖共享 | Import Maps (W3C 标准) |
| 运行环境 | 浏览器原生支持，零构建 |

## 端口

- **后端 API**: http://localhost:8093/api
- **前端页面**: http://localhost:8093/

## 运行环境要求

- JDK 21+
- Maven 3.8+

## 启动方式

```bash
cd 08-原生ESM微前端基础
mvn spring-boot:run
```

然后浏览器打开 http://localhost:8093/

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users | 用户列表 |
| GET | /api/users/{id} | 单个用户 |
| POST | /api/users | 创建用户 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |
| GET | /api/products | 商品列表 |
| GET | /api/products/{id} | 单个商品 |
| POST | /api/products | 创建商品 |
| DELETE | /api/products/{id} | 删除商品 |

## 项目结构

```
08-原生ESM微前端基础/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/esm/
    │   ├── EsmMicroFrontendBasicApplication.java  (启动类)
    │   ├── entity/
    │   │   ├── User.java                          (用户实体)
    │   │   └── Product.java                       (商品实体)
    │   ├── service/
    │   │   ├── UserService.java                   (用户服务)
    │   │   └── ProductService.java                (商品服务)
    │   └── controller/
    │       ├── UserController.java                (用户控制器)
    │       └── ProductController.java             (商品控制器)
    └── resources/
        ├── application.properties                 (端口: 8093)
        └── static/
            ├── index.html                         (Host App 壳)
            ├── import-map.json                    (共享依赖配置)
            ├── css/style.css                      (全局样式)
            └── microapp/app1/
                └── main.js                       (微前端子应用入口)
```

## ESM 微前端优势

1. **零构建** - 无需 Webpack/Vite/Rollup，浏览器原生支持 ES Module
2. **独立开发部署** - 每个微前端是独立 ES Module，可单独开发、独立部署
3. **依赖去重** - Import Maps 保证所有子应用共享同一份 React
4. **懒加载** - 通过 `import()` 按需加载，首屏只加载 Host App
5. **标准化** - 基于 W3C Import Maps 标准，非厂商锁定

## 注意事项

- Import Maps 需要 Chrome 89+ / Edge 89+ / Safari 16.4+ / Firefox 108+
- React 依赖通过 esm.sh CDN 加载
- 生产环境建议将 React 文件本地化到 static 目录
