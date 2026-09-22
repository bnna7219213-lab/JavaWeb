# 04 - HTMX 超媒体驱动基础

最简单的 HTMX 示例项目，演示 `hx-get` / `hx-post` 调用后端，后端返回 HTML 片段（非 JSON），Thymeleaf 模板本地化渲染。

## 技术栈

- JDK 21
- Spring Boot 3.2.5
- Thymeleaf 模板引擎
- HTMX 1.9.10 (CDN 引入)

## 项目结构

```
04-HTMX超媒体驱动基础/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/
    │   ├── HtmxBasicApplication.java    # 启动类
    │   ├── controller/
    │   │   └── UserController.java       # 控制器 - 返回 HTML 片段
    │   ├── service/
    │   │   └── UserService.java          # 用户服务层
    │   └── entity/
    │       └── User.java                 # 用户实体
    └── resources/
        ├── application.properties        # 配置文件
        ├── templates/
        │   ├── index.html                # 主页面（含 HTMX 属性）
        │   └── fragments.html            # HTMX 片段模板
        └── static/
            └── css/
                └── style.css             # 样式文件
```

## 运行说明

### 前置条件
- JDK 21 已安装
- Maven 3.8+

### 启动

```bash
cd 04-HTMX超媒体驱动基础
mvn spring-boot:run
```

启动后访问：http://localhost:8080

## API 接口文档

| 方法 | 路径 | 说明 | HTMX 属性 |
|------|------|------|-----------|
| GET | `/` | 登录主页面 | - |
| GET | `/api/user/fragment` | 获取用户列表 HTML 片段 | `hx-get` |
| GET | `/api/user/{id}/fragment` | 获取单个用户 HTML 片段 | `hx-get` |
| POST | `/api/user/fragment` | 创建用户并返回更新后的列表 | `hx-post` |

## 工作流程

1. **hx-get 加载列表**：按钮设置 `hx-get="/api/user/fragment"` `hx-target="#result1"`，点击后 GET 请求后端，后端 `@Controller` 返回 `"fragments :: userList"` 片段，HTMX 将返回的 HTML 替换到 `#result1`。

2. **hx-post 创建用户**：表单设置 `hx-post="/api/user/fragment"` `hx-target="#result3"`，POST 提交后后端创建用户并返回最新列表片段。

## 核心设计

- 后端使用 `@Controller`（非 `@RestController`），返回视图名称字符串
- Thymeleaf 片段（`th:fragment`）通过 `"fragments :: userList"` 语法引用
- CDN 引入 HTMX：`<script src="https://unpkg.com/htmx.org@1.9.10"></script>`
- 无需 JavaScript，所有交互通过 HTML 属性驱动
