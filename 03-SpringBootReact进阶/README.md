# 类型3 - Spring Boot + React 无构建（进阶版）

## 进阶特性
- 完整 CRUD：GET / POST / PUT / DELETE
- React Router 多路由（列表、详情、新增、编辑）
- Spring @Service 依赖注入
- CORS 跨域配置（WebMvcConfigurer）
- 统一响应封装
- 搜索+模糊匹配

## API接口

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | /api/user?id=1 | id | 查询单个 |
| POST /api/user | JSON Body | 新增 |
| PUT /api/user | JSON Body | 更新 |
| DELETE | /api/user?id=1 | id | 删除 |
| GET | /api/user.list | search? | 全部/搜索 |

## 运行
运行 SpringBootReactAdvancedApplication.java，访问 http://localhost:8084/

## curl测试
```
curl http://localhost:8084/api/user?id=1
curl -X POST http://localhost:8084/api/user -H "Content-Type: application/json" -d '{"name":"赵六","age":26}'
curl -X PUT http://localhost:8084/api/user -H "Content-Type: application/json" -d '{"id":1,"name":"张三三","age":25}'
curl -X DELETE "http://localhost:8084/api/user?id=1"
curl http://localhost:8084/api/user/list?search=张"
```
