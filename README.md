# Spring Modulith 模块化单体 - 基础版

## 项目概述

本项目演示了如何使用 Spring Boot 3.3 + JDK 21 构建模块化单体应用（Modular Monolith）。

核心思想：**在一个 Spring Boot 应用中，通过清晰的包结构和接口隔离，模拟 Spring Modulith 的模块化设计模式，实现模块间的解耦。**

## 技术栈

- JDK 21
- Spring Boot 3.3.5
- Spring Web (端口: 8101)
- Thymeleaf 模板引擎
- Lombok

## 模块化设计原则

### 1. 模块封装（Module Encapsulation）
每个模块是一个独立的包，有明确的边界：

| 模块 | 包路径 | 职责 |
|------|--------|------|
| user | `com.example.modulith.basic.modules.user` | 用户管理 |
| product | `com.example.modulith.basic.modules.product` | 商品管理 |
| order | `com.example.modulith.basic.modules.order` | 订单管理（跨模块） |
| shared | `com.example.modulith.basic.shared` | 共享DTO和事件 |
```

| # | 项目类型 | 基础版 | 进阶版 | 端口 |

|---|---------|--------|--------|------|

| 1 | Servlet传统Web | 01-Servlet传统Web基础 | 01-Servlet传统Web进阶 | Tomcat |

| 2 | React无构建 | 02-React无构建基础 | 02-React无构建进阶 | Tomcat |

| 3 | Spring Boot + React | 03-SpringBootReact基础 | 03-SpringBootReact进阶 | 8083/8084 |

| 4 | HTMX超媒体驱动 | 04-HTMX超媒体驱动基础 | 04-HTMX超媒体驱动进阶 | 8080/8081 |

| 5 | Hilla类型安全全栈 | 05-Hilla类型安全全栈基础 | 05-Hilla类型安全全栈进阶 | 8085/8086 |

| 6 | Lit原生Web组件 | 06-Lit原生Web组件基础 | 06-Lit原生Web组件进阶 | 8091/8092 |

| 7 | Astro岛屿架构 | 07-Astro岛屿架构基础 | 07-Astro岛屿架构进阶 | 8080/8081 |

| 8 | 原生ESM微前端 | 08-原生ESM微前端基础 | 08-原生ESM微前端进阶 | 8093/8094 |

| 9 | 边缘渲染 + BFF | 09-边缘渲染BFF基础 | 09-边缘渲染BFF进阶 | 8095/8096 |

| 10 | WebAssembly高性能 | 10-WebAssembly高性能基础 | 10-WebAssembly高性能进阶 | 8097/8098 |

| 11 | 自包含系统SCS | 11-自包含系统SCS基础 | 11-自包含系统SCS进阶 | 8099/8100 |

| 12 | Spring Modulith单体 | 12-SpringModulith模块化单体基础 | 12-SpringModulith模块化单体进阶 | 8101/8102 |

| 13 | GraalVM原生运行时 | 13-GraalVM原生运行时基础 | 13-GraalVM原生运行时进阶 | 8103/8104 |

```







