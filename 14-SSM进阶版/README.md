# 类型14 - SSM（Spring + SpringMVC + MyBatis）进阶版

## 进阶版特性（相比基础版新增）
- **HikariCP 连接池** — 替代DriverManagerDataSource，生产级高性能连接池
- **AOP 切面日志** — @Aspect 无侵入式 Service 层方法计时
- **全局异常处理器** — @RestControllerAdvice 统一错误格式
- **业务异常体系** — BusinessException + 错误码分类
- **API 拦截器** — HandlerInterceptor 请求耗时日志
- **分页查询** — PageQuery + total/page/size 分页响应
- **驼峰映射** — mapUnderscoreToCamelCase 自动转换
- **多模块CRUD** — 用户 + 订单，跨表JOIN关联查询
- **统计聚合** — SUM/COUNT 聚合函数查询
- **状态管理** — 订单状态流转（PENDING → PAID → SHIPPED → COMPLETED）

## 技术栈
- 前端：HTML5 + CSS3 + Thymeleaf（服务端渲染）+ JavaScript fetch
- 后端：Spring 6.1 + Spring MVC 6.1 + AOP
- 持久层：MyBatis 3.5 + mybatis-spring 3.0
- 连接池：HikariCP 5.1
- 数据库：H2（MySQL 兼容模式）
- 服务器：Tomcat 10+

## 项目结构
```
14-SSM进阶版/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/ssm/
    │   ├── entity/                  # 实体 (User, Order)
    │   ├── mapper/                  # 数据访问层 (接口+XML)
    │   ├── service/                 # 业务层 (接口+实现)
    │   ├── controller/              # 控制器 (Page + API)
    │   │   ├── PageController.java  # Thymeleaf 页面路由
    │   │   ├── UserApiController.java
    │   │   └── OrderApiController.java
    │   ├── common/                  # 通用工具
    │   │   ├── Result.java          # 统一响应
    │   │   ├── PageQuery.java       # 分页参数
    │   │   └── ApiInterceptor.java  # 拦截器
    │   ├── aspect/                  # AOP切面
    │   │   └── ServiceLogAspect.java
    │   └── exception/               # 异常处理
    │       ├── BusinessException.java
    │       └── GlobalExceptionHandler.java
    ├── resources/
    │   ├── db.properties            # 数据库配置
    │   ├── schema.sql               # 建表脚本（含初始数据）
    │   ├── mapper/                  # MyBatis SQL映射
    │   └── spring/
    │       ├── applicationContext.xml
    │       └── springmvc.xml
    └── webapp/
        ├── WEB-INF/
        │   ├── web.xml
        │   └── templates/           # Thymeleaf 模板
        │       ├── index.html       # 仪表盘
        │       ├── users.html       # 用户管理
        │       └── orders.html      # 订单管理
        └── static/                  # CSS/JS
```

## 用户 API 接口

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | /api/user?id=1 | id | 查询单个 |
| GET | /api/user/list | keyword? | 查询全部/搜索 |
| POST | /api/user | JSON Body | 新增用户 |
| PUT | /api/user | JSON Body | 更新用户 |
| DELETE | /api/user?id=1 | id | 删除用户 |

## 订单 API 接口

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | /api/order?id=1 | id | 查询单个 |
| GET | /api/order/list | userId?, status?, page, size? | 分页查询 |
| POST | /api/order | JSON Body | 新增订单 |
| PATCH | /api/order/status?id=1&status=PAID | id + status | 更新状态 |
| DELETE | /api/order?id=1 | id | 删除订单 |
| GET | /api/order/stats | - | 统计汇总 |

## 页面路由

| 路径 | 说明 |
|------|------|
| `/` 或 `/index.html` | 仪表盘（统计数据） |
| `/users.html` | 用户管理页 |
| `/orders.html` | 订单管理页 |

## 运行方式
部署到 Tomcat 10+，访问 http://localhost:8080/ssm-advanced/

## 接口调用示例

用户 CRUD：
```bash
curl http://localhost:8080/ssm-advanced/api/user/list
curl -X POST http://localhost:8080/ssm-advanced/api/user -H "Content-Type: application/json" -d '{"name":"赵六","age":26}'
curl -X PUT http://localhost:8080/ssm-advanced/api/user -H "Content-Type: application/json" -d '{"id":1,"name":"张三三","age":25}'
curl -X DELETE "http://localhost:8080/ssm-advanced/api/user?id=1"
```

订单 CRUD（含分页、筛选、统计）：
```bash
curl "http://localhost:8080/ssm-advanced/api/order/list?page=1&size=10"
curl "http://localhost:8080/ssm-advanced/api/order/list?userId=1&status=PAID"
curl -X POST http://localhost:8080/ssm-advanced/api/order -H "Content-Type: application/json" -d '{"userId":1,"product":"iPhone","amount":6999.00}'
curl -X PATCH "http://localhost:8080/ssm-advanced/api/order/status?id=1&status=PAID"
curl http://localhost:8080/ssm-advanced/api/order/stats
```
