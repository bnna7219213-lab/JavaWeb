# 06 - Lit 原生 Web 组件进阶版

> 完整 CRUD 系统，含前端路由（hashchange 事件监听）、多个自定义组件、组件间通信

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| JDK | 21 | Java 开发工具箱 |
| Spring Boot | 3.3.4 | 静态资源服务器 + REST API + 内嵌 Tomcat |
| 前端 | Lit 3.1.0 | Web Components 标准官方封装（CDN 引入） |
| 构建 | 零构建 | 通过 ESM CDN + Import Maps 管理依赖 |

## 项目结构

```
06-Lit原生Web组件进阶/
├── pom.xml                                   # Maven 配置
├── README.md
└── src/main/
    ├── java/com/example/
    │   ├── LitWebAdvancedApplication.java          # 启动类
    │   ├── controller/
    │   │   └── UserController.java                 # REST 控制器 (完整 CRUD)
    │   ├── service/
    │   │   └── UserService.java                    # 业务逻辑（内存存储）
    │   ├── entity/
    │   │   └── User.java                           # 用户实体
    │   └── config/
    │       └── WebConfig.java                      # CORS 配置
    └── resources/
        ├── application.properties                   # 应用配置
        └── static/
            ├── index.html                            # 入口页面（含 Import Maps）
            └── frontend/
                ├── main.js                           # 前端入口
                ├── components/
                │   └── app-router.js                 # 路由管理器 + EventBus
                ├── pages/
                │   ├── home-page.js                  # 首页
                │   ├── user-list-page.js             # 用户列表页
                │   ├── user-detail-page.js           # 用户详情页
                │   ├── user-form-page.js              # 用户表单（新增/编辑）
                │   └── not-found-page.js             # 404 页面
                └── utils/
                    ├── request.js                     # 请求工具函数
                    └── toast.js                       # Toast 通知
```

## 启动方式

```bash
cd 06-Lit原生Web组件进阶

# Maven 运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/lit-web-advanced-1.0.0.jar
```

启动后访问: **http://localhost:8092**

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user?id=1` | 查询单个用户 |
| POST | `/api/user` | 创建用户 |
| PUT | `/api/user` | 更新用户 |
| DELETE | `/api/user?id=1` | 删除用户 |
| GET | `/api/user/list` | 查询全部用户 |
| GET | `/api/user/list?search=张` | 搜索用户 |

## 路由表

| Hash 路由 | 页面组件 | 说明 |
|-----------|---------|------|
| `#/` | `<home-page>` | 首页，欢迎与介绍 |
| `#/users` | `<user-list-page>` | 用户列表、搜索、删除 |
| `#/users/new` | `<user-form-page>` | 新增用户表单 |
| `#/users/:id` | `<user-detail-page>` | 用户详情页 |
| `#/users/:id/edit` | `<user-form-page>` | 编辑用户表单 |

## Lit 进阶特性演示

### 1. Shadow DOM 样式隔离

```javascript
static styles = css`
    :host { display: block; }
    .card { border: 1px solid #eee; }
`;
```

### 2. 组件事件通信（dispatchEvent）

```javascript
// 子组件派发事件
this.dispatchEvent(new CustomEvent('user-delete', {
    detail: { id: this.id, name: this.user.name },
    bubbles: true,
    composed: true
}));
```

### 3. EventBus 发布/订阅模式

```javascript
// 订阅
eventBus.on(EVENTS.USER_CREATED, (data) => { ... });

// 发布
eventBus.emit(EVENTS.USER_CREATED, { user });
```

### 4. Hash 路由监听

```javascript
window.addEventListener('hashchange', () => router.resolve());
```

### 5. 表单双向绑定模拟

```javascript
// 通过 @input 事件更新响应式属性
_handleInput(field, e) {
    this.formData = { ...this.formData, [field]: e.target.value };
}
```

## 注意事项

- 需要联网（Lit 库通过 CDN 加载）
- 浏览器需支持 ES Modules 和 Import Maps（现代浏览器均支持）
- 内存存储，重启后数据重置
