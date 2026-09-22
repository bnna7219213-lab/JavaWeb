# 类型3 - Spring Boot + React 无构建

## 架构说明
Spring Boot 内嵌 Tomcat，React 代码放入 static 目录，作为静态资源直接交付。
不需要 Node.js 构建，Maven 打包后一个 jar 包含全部。

## 技术栈
- 后端：Spring Boot 3 + @RestController
- 前端：React 18 ESM + htm + Import Maps（零构建）

## API接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/user | 查询用户 |
| POST | /api/user/add | 新增用户 |

## 运行方式
1. 右键 SpringBootReactBasicApplication.java → Run As → Java Application
2. 访问 http://localhost:8083/

## 接口调用
```
curl http://localhost:8083/api/user
curl -X POST http://localhost:8083/api/user/add -H "Content-Type: application/json" -d '{"name":"李四","age":30}'
```
