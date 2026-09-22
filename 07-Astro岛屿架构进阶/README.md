# 07 - Astro 岛屿架构进阶

## 项目说明

本项目是 **Astro 岛屿架构 (Islands Architecture)** 的完整模拟实现。通过 Spring Boot + Thymeleaf
实现服务端渲染（替代 Astro 的 .astro 文件），结合 Alpine.js 和 React via CDN 展现完整的岛屿架构设计。

## 岛屿架构概念

### 核心理念

```
传统 SPA 模式：
  整个页面 -> 全部打包为 JS -> 全部水化 -> 全部执行
  JS 体积：200KB ~ 1MB+
  FCP：1.5 ~ 4s

岛屿模式 (Astro)：
  页面拆分为：
    ├── 静态 HTML（服务端渲染，零 JS）
    ├── 小型交互岛屿（Alpine.js，~15KB）
    └── 复杂交互岛屿（React/Vue via CDN）
  JS 体积：0 ~ 20KB
  FCP：0.2 ~ 0.8s
```

### 水化策略

| 岛屿类型 | 加载时机 | 代表技术 | 适用场景 |
|----------|----------|----------|----------|
| static | 服务端渲染 | Thymeleaf | 文章、列表展示 |
| client:load | 立即水化 | Alpine.js | 搜索、折叠、Tab |
| client:idle | 空闲时水化 | Alpine.js | 评论、推荐 |
| client:media | 视口可见时 | React | 图表、购物车 |

### 本项目的映射

| Astro | 本项目 |
|-------|--------|
| .astro 文件 | Thymeleaf 模板 + Controller |
| `---` 服务端代码 | Spring Boot Controller 模型 |
| `<X client:load />` | `<div x-data="">` |
| `<X client:idle />` | `<div x-data="..." x-init="">` |
| `<X client:visible />` | React via CDN + 事件触发 |
| 默认零 JS | Thymeleaf th:* 输出静态 HTML |

## 项目结构

```
07-Astro岛屿架构进阶/
├── pom.xml                           # Spring Boot 3.2 + Thymeleaf
├── README.md
└── src/main/
    ├── java/com/example/astro/
    │   ├── AstroIslandsAdvancedApplication.java
    │   ├── entity/
    │   │   ├── User.java             # 用户实体（含 department）
    │   │   └── Product.java          # 产品实体（含 stock, rating）
    │   └── controller/
    │       ├── PageController.java   # 5 个页面路由
    │       └── ApiController.java    # RESTful JSON API
    └── resources/
        ├── application.properties
        ├── templates/
        │   ├── layouts/base.html     # 公共布局
        │   ├── pages/
        │   │   ├── index.html        # 首页（3 Alpine + 1 React）
        │   │   ├── users.html        # 用户页（Alpine filter + React table）
        │   │   ├── products.html     # 产品页（React cart + Alpine sort）
        │   │   ├── dashboard.html    # 仪表盘（static KPI + React + Alpine）
        │   │   └── user-analysis.html # 用户分析（Alpine + React + Alpine）
        │   └── fragments/
        │       └── user-card.html    # 卡片片段（4 版本）
        └── static/
            └── css/style.css         # 完整样式
```

## 页面与岛屿矩阵

| 页面 | 静态内容 | Alpine 岛屿 | React 岛屿 |
|------|----------|-------------|------------|
| index | Hero + 说明 | 折叠面板 + 搜索 + Tab | 购物车 |
| users | 用户列表 | 多条件筛选 | 分页表格 |
| products | 产品网格 | 排序筛选 | 购物车 |
| dashboard | KPI 卡片 | 实时刷新 | 图表 |
| user-analysis | 数据表格 | 部门Tab + 批量操作 | 分布图 |

## API 接口

```
GET    /api/users                    # 全部用户
GET    /api/users/search?keyword=    # 搜索用户
GET    /api/users/by-department     # 按部门分组
GET    /api/users/{id}              # 单个用户
GET    /api/products                # 全部产品
GET    /api/products/top           # 评分最高
GET    /api/stats                  # 统计数据
POST   /api/users                  # 创建用户
PUT    /api/users/{id}             # 更新用户
DELETE /api/users/{id}             # 删除用户
```

## 运行

```bash
cd 07-Astro岛屿架构进阶
mvn spring-boot:run
# 端口 8081（避免与基础版冲突）
```

访问地址：
- http://localhost:8081/ — 首页
- http://localhost:8081/users — 用户管理
- http://localhost:8081/products — 产品管理
- http://localhost:8081/dashboard — 仪表盘
- http://localhost:8081/user-analysis — 用户分析

## CDN 依赖

| 库 | 体积(gzip) | 用途 |
|----|------------|------|
| Alpine.js 3.x | ~12KB | 简单交互岛屿 |
| React 18 (UMD) | ~30KB (single file) | 复杂交互岛屿 |
| Babel Standalone | (dev only) | JSX 转译 (生产用预编译) |
