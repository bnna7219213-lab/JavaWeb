# 07 - Astro 岛屿架构基础

## 项目说明

本项目使用 Java + Spring Boot + Thymeleaf 模拟 **Astro 的岛屿架构 (Islands Architecture)** 核心理念。

## 岛屿架构核心理念

Astro 的岛屿架构遵循以下原则：

1. **默认所有内容都是零 JS 的静态 HTML** — 服务端直接渲染完整页面
2. **交互式"岛屿"按需水化** — 只在需要交互的局部区域加载 JS
3. **极致首屏性能** — HTML 到达即可显示，不依赖任何 JS 执行

### 类比说明

```
传统 SPA:        整个页面是一个巨大的 JS 应用，全部水化
                  字节数: ~200KB+ JS, FCP: 2-4s

Astro 岛屿:      静态 HTML 直接显示，只有标记为岛屿的区域加载 JS
                  字节数: ~5KB JS, FCP: 0.3-0.8s
```

### 本项目的映射关系

| Astro 概念 | 本项目实现 |
|---|---|
| 服务端渲染 .astro 文件 | Thymeleaf 模板引擎渲染 HTML |
| `<Component client:load />` | 带有 x-data 属性的 `<div>` |
| `<Component client:idle />` | Alpine.js 组件按需水化 |
| `---` 中服务端代码 | Spring Controller 传递模型数据 |
| 静态内容零 JS | Thymeleaf th:* 属性直接输出 HTML |
| Island 组件 | Alpine.js 交互组件 |

## 项目结构

```
07-Astro岛屿架构基础/
├── pom.xml                          # Maven 配置（Spring Boot 3.2 + Thymeleaf）
├── README.md                        # 项目说明
└── src/main/
    ├── java/com/example/astro/
    │   ├── AstroIslandsBasicApplication.java  # 启动类
    │   ├── entity/
    │   │   ├── User.java                      # 用户实体
    │   │   └── Product.java                   # 产品实体
    │   └── controller/
    │       ├── PageController.java            # 页面路由（返回 View）
    │       └── ApiController.java             # API 接口（返回 JSON）
    └── resources/
        ├── application.properties             # 应用配置
        ├── templates/
        │   ├── layouts/base.html              # 公共布局
        │   ├── pages/
        │   │   ├── index.html                 # 首页（含3个交互岛屿）
        │   │   └── users.html                 # 用户页（含2个交互岛屿）
        │   └── fragments/
        │       └── user-card.html             # HTML 片段
        └── static/
            └── css/style.css                  # 全局样式
```

## 交互岛屿说明

### 首页岛屿

1. **Alpine.js 折叠面板** — 点击按钮展开/收起架构说明
2. **Alpine.js 计数器** — +/- 计数器交互
3. **Alpine.js Tab 切换** — 三类信息 Tab 切换

### 用户页岛屿

1. **客户端搜索岛屿** — 调用 /api/users/search 异步获取数据
2. **用户详情展开** — 点击卡片查看详情（纯静态内容展开）

## 运行方式

```bash
# 进入项目目录
cd 07-Astro岛屿架构基础

# 编译并运行
mvn spring-boot:run

# 访问
# http://localhost:8080/         — 首页（含3个交互岛屿）
# http://localhost:8080/users    — 用户页（含2个交互岛屿）
# http://localhost:8080/api/users — API JSON 数据
# http://localhost:8080/api/products — 产品 API
```

## 性能优势

- 首屏 HTML 包含所有可见内容，搜索引擎可直接爬取
- 无需等待 JS 执行即可看到完整页面
- Alpine.js 体积极小 (~15KB gzip)，仅加载一次
- 后续页面切换可通过 HTMX/Alpine实现 SPA 体验

## 学习路径

1. 先理解 Thymeleaf 服务端渲染（纯静态 HTML 输出）
2. 再理解 Alpine.js 岛屿（按需水化）
3. 进阶版将引入 React CDN + 多层级岛屿交互
