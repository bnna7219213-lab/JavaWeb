# 类型1 - Servlet 传统Web架构（基础版）

## 架构说明
标准的 Eclipse Dynamic Web Project 结构，前端纯 HTML + 后端原生 Servlet，
通过 web.xml 配置 Servlet 映射和欢迎页，前后端通过 fetch + JSON 交互。

## 技术栈
- 前端：原生 HTML5 + CSS3 + JavaScript（fetch API）
- 后端：原生 Servlet 4.0
- 服务器：Tomcat 9+

## 项目结构
```
src/main/
├── java/com/example/servlet/UserServlet.java
└── webapp/
    ├── index.html
    └── WEB-INF/web.xml
```

## API接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/user | 查询用户信息 |
| POST | /api/user | 提交用户数据 |

## 运行方式
1. 导入 Eclipse：File → Import → Existing Maven Project
2. 配置 Tomcat 9 服务器
3. 右键项目 → Run As → Run on Server
4. 访问 http://localhost:8080/servlet-web-basic/

## 接口调用示例

GET查询：
```
curl http://localhost:8080/servlet-web-basic/api/user
```

POST提交：
```
curl -X POST http://localhost:8080/servlet-web-basic/api/user \
  -H "Content-Type: application/json" \
  -d '{"name":"李四","age":30}'
```
