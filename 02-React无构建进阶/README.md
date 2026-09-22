# 类型2 - React 无构建方案（进阶版）

## 进阶特性
- React Router HashRouter 实现多页面SPA路由
- 完整 CRUD：列表查看、详情、创建、删除
- Service 层完整分离，与 Servlet 解耦
- CORS 跨域过滤器
- Search 模糊搜索功能

## API接口

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | /api/user?id=1 | id | 查询单个用户 |
| POST | /api/user | JSON Body | 新增用户 |
| DELETE | /api/user?id=1 | id | 删除用户 |
| GET | /api/user/list | search(可选) | 查询全部/搜索 |

## 目录结构
```
02-React无构建进阶/
├── pom.xml
└── src/main/
    ├── java/com/example/
    │   ├── entity/User.java
    │   ├── servlet/{UserServlet,UserListServlet}.java
    │   ├── service/UserService.java
    │   └── util/{JsonUtil,JsonResult,CorsFilter}.java
    └── webapp/
        ├── index.html
        ├── WEB-INF/web.xml
        └── frontend/
            ├── main.js, App.js
            ├── utils/{react-html,request}.js
            └── pages/{Home,UserList,UserDetail,UserCreate}.js
```

## 运行方式
部署到Tomcat后访问 http://localhost:8080/react-nobuild-advanced/
