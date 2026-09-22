# 04 - HTMX 超媒体驱动进阶

完整的 HTMX CRUD 系统，包含用户管理和订单管理两大模块，演示 HTMX 的高级特性和工程化实践。

## 技术栈

- JDK 21
- Spring Boot 3.2.5
- Thymeleaf 模板引擎
- HTMX 1.9.10 (CDN 引入)

## 项目结构

```
04-HTMX超媒体驱动进阶/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/
    │   ├── HtmxAdvancedApplication.java    # 启动类
    │   ├── controller/
    │   │   ├── PageController.java          # 页面路由（仪表盘）
    │   │   ├── UserController.java          # 用户管理接口
    │   │   └── OrderController.java         # 订单管理接口
    │   ├── service/
    │   │   ├── UserService.java             # 用户服务层
    │   │   └── OrderService.java            # 订单服务层
    │   └── entity/
    │       ├── User.java                    # 用户实体 (id, name, age, email)
    │       └── Order.java                   # 订单实体 (id, orderNo, userId, productName, amount, status, createTime)
    └── resources/
        ├── application.properties           # 配置文件
        ├── templates/
        │   ├── dashboard.html               # 仪表盘页面
        │   ├── user/
        │   │   ├── index.html               # 用户管理页面
        │   │   └── fragments.html           # 用户片段（table / row / form / empty）
        │   └── order/
        │       ├── index.html               # 订单管理页面
        │       └── fragments.html           # 订单片段（table / row / form / empty）
        └── static/
            └── css/
                └── style.css                # 全局样式
```

## 运行说明

### 前置条件
- JDK 21 已安装
- Maven 3.8+

### 启动

```bash
cd 04-HTMX超媒体驱动进阶
mvn spring-boot:run
```

启动后访问：**http://localhost:8081**

## API 接口文档

### 用户管理

| 方法 | 路径 | 说明 | HTMX 特性 |
|------|------|------|-----------|
| GET | `/users` | 用户管理页面 | - |
| GET | `/users/table` | 用户表格片段 | `hx-get` |
| GET | `/users/new` | 新建用户表单 | `hx-get` + `hx-swap="innerHTML"` |
| GET | `/users/{id}/edit` | 编辑用户表单 | `hx-get` + `hx-swap="outerHTML"` |
| GET | `/users/{id}/row` | 单行用户片段 | `hx-get` + `hx-swap="outerHTML"` |
| GET | `/users/search?keyword=xxx` | 搜索用户 | `hx-trigger="input changed delay:300ms"` 防抖 |
| POST | `/users/save` | 创建/更新用户 | `hx-post` + `hx-swap="outerHTML"` |
| DELETE | `/users/{id}` | 删除用户 | `hx-delete` + `hx-confirm` + `hx-swap="outerHTML"` |

### 订单管理

| 方法 | 路径 | 说明 | HTMX 特性 |
|------|------|------|-----------|
| GET | `/orders` | 订单管理页面 | - |
| GET | `/orders/table` | 订单表格片段 | `hx-get` |
| GET | `/orders/new` | 新建订单表单 | `hx-get` + `hx-swap="innerHTML"` |
| GET | `/orders/{id}/row` | 单行订单片段 | `hx-swap="outerHTML"` |
| GET | `/orders/by-user/{userId}` | 按用户筛选订单 | `hx-get` |
| POST | `/orders/save` | 创建订单 | `hx-post` |
| POST | `/orders/{id}/status` | 更新订单状态 | `hx-post` + `hx-vals` |
| DELETE | `/orders/{id}` | 删除订单 | `hx-delete` + `hx-confirm` |

### 仪表盘

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 仪表盘首页（统计+最近数据+事件日志） |

## HTMX 高级用法说明

### 1. hx-swap="outerHTML" 行级更新
编辑或删除某行时，后端返回整行 `<tr>` 片段或空片段，HTMX 用 outerHTML 模式替换/移除整行元素，无需刷新整个表格。

### 2. hx-trigger 防抖搜索
搜索框使用 `hx-trigger="input changed delay:300ms"`，用户停止输入 300ms 后才发起请求，避免频繁查询。

### 3. hx-confirm 删除确认
删除按钮绑定 `hx-confirm="确认消息"`，用户点击后弹出浏览器原生确认对话框，确认后才发起 DELETE 请求。

### 4. hx-vals 传递额外参数
状态更新按钮使用 `hx-vals='{"status": "已支付"}'` 在 POST 请求体中携带额外参数。

### 5. HTMX 全局事件
仪表盘页面监听 `htmx:beforeRequest`、`htmx:afterSwap`、`htmx:responseError` 事件，实时输出请求日志。

### 6. 表单验证
HTML5 表单验证（`required`, `minlength`, `maxlength`, `min`, `max`, `step`, `type="email"`），提交前浏览器自动校验。

## 工作流程示例

### 编辑用户
1. 点击某行的"编辑"按钮 -> `hx-get="/users/{id}/edit"` ->
2. `hx-swap="outerHTML"` 将该行替换为内联表单
3. 修改后点击"保存" -> `hx-post="/users/save"` ->
4. 后端保存后返回 `userRow` 片段 -> outerHTML 替换回普通行

### 删除订单
1. 点击"删除" -> `hx-confirm` 弹出确认 ->
2. 确认后 `hx-delete="/orders/{id}"` ->
3. 后端返回空片段 -> outerHTML 将该行从 DOM 移除

### 搜索用户
1. 在搜索框输入字符 -> 每输入一个字符触发 input 事件 ->
2. `hx-trigger="input changed delay:300ms"` 防抖 ->
3. 300ms 后发起 GET 请求 -> 后端返回更新后的表格

## 设计原则

- **后端返回 HTML**：所有 API 返回 Thymeleaf 片段（非 JSON），由前端 HTMX 直接插入 DOM
- **零手写 JS**：除事件监听外无需 JavaScript
- **渐进增强**：页面在无 JS 时表单仍可正常提交
- **片段复用**：同一片段模板在多处被引用
