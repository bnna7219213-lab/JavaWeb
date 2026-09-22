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



\---



\*\*14-SSM基础版\*\* — 经典 XML 配置，Thymeleaf HTML 页面：



\- \[pom.xml](C:/Users/bnna7/Aether/javaClasses/14-SSM基础版/pom.xml) — Spring 6.1 + MyBatis 3.5 + Thymeleaf + H2

\- \[web.xml](C:/Users/bnna7/Aether/javaClasses/14-SSM基础版/src/main/webapp/WEB-INF/web.xml) — 双容器配置（ContextLoaderListener + DispatcherServlet）

\- \[applicationContext.xml](C:/Users/bnna7/Aether/javaClasses/14-SSM基础版/src/main/resources/spring/applicationContext.xml) — DataSource + SqlSessionFactory + Mapper扫描 + 事务

\- \[springmvc.xml](C:/Users/bnna7/Aether/javaClasses/14-SSM基础版/src/main/resources/spring/springmvc.xml) — MVC注解驱动 + Thymeleaf视图解析器

\- \[UserMapper.xml](C:/Users/bnna7/Aether/javaClasses/14-SSM基础版/src/main/resources/mapper/UserMapper.xml) — MyBatis SQL映射（含useGeneratedKeys回填主键）

\- \[index.html](C:/Users/bnna7/Aether/javaClasses/14-SSM基础版/src/main/webapp/WEB-INF/templates/index.html) — Thymeleaf模板（用户列表）

\- \[README.md](C:/Users/bnna7/Aether/javaClasses/14-SSM基础版/README.md) — 完整API文档和调用示例



\---



\*\*14-SSM进阶版\*\* — 增强功能（AOP/异常处理/多模块/分页/连接池）：



\- \[pom.xml](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/pom.xml) — 额外增加 HikariCP + AspectJ + Hibernate Validator

\- \[applicationContext.xml](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/src/main/resources/spring/applicationContext.xml) — HikariCP连接池 + AOP自动代理 + 驼峰映射

\- \[Result.java](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/src/main/java/com/example/ssm/common/Result.java) — 统一响应封装（支持分页total/page/size）

\- \[ServiceLogAspect.java](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/src/main/java/com/example/ssm/aspect/ServiceLogAspect.java) — AOP切面无侵入式Service计时

\- \[GlobalExceptionHandler.java](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/src/main/java/com/example/ssm/exception/GlobalExceptionHandler.java) — @RestControllerAdvice全局异常处理

\- \[OrderMapper.xml](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/src/main/resources/mapper/OrderMapper.xml) — 用户-订单跨表JOIN关联查询 + 聚合统计

\- \[index.html](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/src/main/webapp/WEB-INF/templates/index.html) — 仪表盘（统计数据 + 架构特性）

\- \[users.html](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/src/main/webapp/WEB-INF/templates/users.html) — 用户管理页（搜索 + 新增）

\- \[orders.html](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/src/main/webapp/WEB-INF/templates/orders.html) — 订单管理页（筛选 + 状态标签）

\- \[README.md](C:/Users/bnna7/Aether/javaClasses/14-SSM进阶版/README.md) — 完整API文档



\---





