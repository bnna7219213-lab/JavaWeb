# 类型1 - Servlet 传统Web架构（进阶版）

## 进阶版特性
- 完整分层：Servlet → Service → Entity，职责清晰
- 统一响应封装 JsonResult（code/msg/data 三字段标准格式）
- JSON工具类（基于Gson）
- CORS跨域过滤器
- RESTful风格URL设计
- 资源级（/api/user）和集合级（/api/user/list）接口分离

## API接口文档

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | /api/user?id=1 | id: 用户ID | 查询单个用户 |
| POST | /api/user | JSON Body | 新增用户 |
| DELETE | /api/user?id=1 | id: 用户ID | 删除用户 |
| GET | /api/user/list | search(可选) | 查询全部/搜索用户 |

## 运行方式
同基础版，部署到Tomcat后访问 http://localhost:8080/servlet-web-advanced/

## 接口调用示例

查询单个用户：
```
curl http://localhost:8080/servlet-web-advanced/api/user?id=1
```

查询全部用户：
```
curl http://localhost:8080/servlet-web-advanced/api/user/list
```

搜索用户：
```
curl "http://localhost:8080/servlet-web-advanced/api/user/list?search=张"
```

新增用户：
```
curl -X POST http://localhost:8080/servlet-web-advanced/api/user \
  -H "Content-Type: application/json" \
  -d '{"name":"赵六","age":26,"email":"zhaoliu@example.com"}'
```

删除用户：
```
curl -X DELETE "http://localhost:8080/servlet-web-advanced/api/user?id=1"
```
