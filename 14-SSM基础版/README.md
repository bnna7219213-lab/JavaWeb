# 类型14 - SSM（Spring + SpringMVC + MyBatis）基础版

## 架构说明
经典的传统 SSM 架构，使用纯 **XML 配置**（无 Spring Boot 自动装配），
展示最原始的 Spring + SpringMVC + MyBatis 整合方式。

- **Spring** — IoC容器 + 事务管理（applicationContext.xml）
- **SpringMVC** — 前端控制器 DispatcherServlet + 视图解析（springmvc.xml）
- **MyBatis** — ORM 映射（Mapper接口 + XML SQL）
- **Thymeleaf** — HTML 模板引擎（替代传统 JSP）
- **H2** — 内存数据库（零配置开箱即用）

## 技术栈
- 前端：HTML5 + CSS3 + Thymeleaf（服务端渲染）+ JavaScript fetch
- 后端：Spring 6.1 + Spring MVC 6.1
- 持久层：MyBatis 3.5 + mybatis-spring 3.0
- 数据库：H2（MySQL 兼容模式）
- 服务器：Tomcat 10+

## 项目结构
```
14-SSM基础版/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/ssm/
    │   ├── entity/User.java                # 实体
    │   ├── mapper/UserMapper.java          # Mapper接口
    │   ├── service/UserService.java        # 业务接口
    │   ├── service/impl/UserServiceImpl.java  # 业务实现
    │   ├── controller/
    │   │   ├── PageController.java          # 页面路由（返回HTML）
    │   │   └── UserApiController.java      # REST API（返回JSON）
    ├── resources/
    │   ├── db.properties                    # 数据库连接配置
    │   ├── schema.sql                       # 建表+初始化数据
    │   ├── mapper/UserMapper.xml            # MyBatis SQL映射
    │   └── spring/
    │       ├── applicationContext.xml       # 根容器配置
    │       └── springmvc.xml               # MVC子容器配置
    └── webapp/
        ├── WEB-INF/
        │   ├── web.xml                      # Web应用配置
        │   └── templates/
        │       ├── index.html               # 首页（用户列表）
        │       └── detail.html              # 详情页
        └── static/
            ├── css/style.css
            └── js/app.js
```

## API 接口文档

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | /api/user?id=1 | id | 查询单个用户 |
| GET | /api/user/list | search(可选) | 查询全部/搜索 |
| POST | /api/user | JSON Body | 新增用户 |
| PUT | /api/user | JSON Body | 更新用户 |
| DELETE | /api/user?id=1 | id | 删除用户 |

## 页面路由

| 路径 | 说明 |
|------|------|
| `/` 或 `/index.html` | 首页（用户列表） |
| `/user/detail?id=1` | 用户详情页 |

## 运行方式

### 方式1：IDE（Eclipse/IDEA）
1. 导入为 **Existing Maven Project**
2. 在 Tomcat 10 上部署运行
3. 访问 http://localhost:8080/ssm-basic/

### 方式2：Maven 命令
```bash
mvn clean package
# 将 target/ssm-basic.war 部署到 Tomcat
```

## 接口调用示例
```bash
# 查询全部
curl http://localhost:8080/ssm-basic/api/user/list

# 搜索
curl "http://localhost:8080/ssm-basic/api/user/list?search=张"

# 新增
curl -X POST http://localhost:8080/ssm-basic/api/user \
  -H "Content-Type: application/json" \
  -d '{"name":"赵六","age":26,"email":"zhaoliu@example.com"}'

# 更新
curl -X PUT http://localhost:8080/ssm-basic/api/user \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"张三三","age":25,"email":"zs2@example.com"}'

# 删除
curl -X DELETE "http://localhost:8080/ssm-basic/api/user?id=1"
```
