# 类型2 - React 无构建方案（基础版）

## 架构核心
利用浏览器原生 ESM + Import Maps，直接从 CDN 加载 React，全程不需要 Node.js、Webpack/Vite。
无缝嵌入传统 Servlet Web 项目，后端接口不变，只是把纯 HTML 前端替换成 React 组件化前端。

## 技术栈
- 前端：React 18（ESM CDN）+ htm 模板引擎
- 后端：原生 Servlet 4.0
- 服务器：Tomcat 9+

## API接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/user | 查询用户 |

## 运行方式
部署到Tomcat后访问 http://localhost:8080/react-nobuild-basic/

## 关键设计
- htm 绑定 createElement 替代 JSX 编译，性能远高于在线 Babel
- importmap 让 `import 'react'` 指向 CDN 的 ESM 地址
- 组件间导入必须写完整 `.js` 后缀（浏览器原生ESM要求）
