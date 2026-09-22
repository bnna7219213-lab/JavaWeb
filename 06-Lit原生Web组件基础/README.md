# 06 - Lit 原生 Web 组件基础版

> 最简单的 Lit 项目，自定义 `user-card` 组件，通过 fetch 获取后端数据

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| JDK | 21 | Java 开发工具包 |
| Spring Boot | 3.3.4 | 静态资源服务器 + REST API |
| 前端 | Lit 3.1.0 | Web Components 标准官方封装（CDN 引入） |
| 构建 | 零构建 | 通过 ESM CDN + Import Maps 管理依赖 |

## 项目结构

```
06-Lit原生Web组件基础/
├── pom.xml                          # Maven 配置（Spring Boot Starter Web）
├── README.md
└── src/main/
    ├── java/com/example/
    │   ├── LitWebBasicApplication.java    # 启动类
    │   ├── controller/
    │   │   └── UserController.java        # REST 控制器
    │   └── entity/
    │       └── User.java                  # 用户实体
    └── resources/
        ├── application.properties          # 应用配置
        └── static/
            ├── index.html                  # 入口页面（含 Import Maps）
            └── frontend/
                ├── main.js                 # 前端入口
                ├── components/
                │   └── user-card.js        # Lit 自定义组件
                └── utils/
                    └── request.js          # 请求工具函数
```

## 启动方式

```bash
# 进入项目目录
cd 06-Lit原生Web组件基础

# Maven 运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/lit-web-basic-1.0.0.jar
```

启动后访问: **http://localhost:8091**

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user?id=1` | 查询单个用户 |
| GET | `/api/user/list` | 查询全部用户 |
| POST | `/api/user/add` | 添加用户 |

## Lit 核心概念演示

### 1. 自定义组件定义

```javascript
import { LitElement, html, css } from 'lit';

class UserCard extends LitElement {
    static properties = { user: { type: Object } };
    
    static styles = css`
        .card { border: 1px solid #eee; padding: 16px; border-radius: 8px; }
    `;
    
    render() {
        return html`
            <div class="card">
                <h3>${this.user.name}</h3>
                <p>年龄: ${this.user.age}</p>
            </div>
        `;
    }
}

customElements.define('user-card', UserCard);
```

### 2. Import Maps 管理依赖

```html
<script type="importmap">
{
    "imports": {
        "lit": "https://esm.sh/lit@3.1.0",
        "lit-html": "https://esm.sh/lit-html@3.1.0"
    }
}
</script>
```

### 3. Shadow DOM 样式隔离

每个 Lit 组件内部样式封装在 Shadow DOM 中，不会污染全局样式。

## 注意事项

- 需要联网（Lit 库通过 CDN 加载）
- 浏览器需支持 ES Modules 和 Import Maps（现代浏览器均支持）
