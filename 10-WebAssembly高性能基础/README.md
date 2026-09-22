# 10-WebAssembly高性能基础

## 项目概述

这是一个演示 WebAssembly 在 Java 全栈项目中应用的模拟项目。后端使用 Spring Boot 提供数据和 API 服务，前端通过 WebAssembly 执行计算密集型逻辑。

## 架构原理

```
┌─────────────────────────────────────────────────────┐
│                    Java 后端 (Spring Boot)           │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────┐  │
│  │ 数据服务层   │  │  REST API    │  │ 业务验证层  │  │
│  └─────────────┘  └──────────────┘  └────────────┘  │
│           HTTP / JSON  / 端口 8097                    │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│                   浏览器端                           │
│  ┌──────────────┐  ┌─────────────────────────────┐  │
│  │  UI 层       │  │  WASM 计算引擎 (.wasm)       │  │
│  │  (HTML/JS)   │  │  - 斐波那契计算              │  │
│  └──────────────┘  │  - 质数筛选                  │  │
│                     │  - 图像卷积                  │  │
│                     │  ▼ 近原生速度执行             │  │
│                     └─────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

## WebAssembly 架构原理

WebAssembly (WASM) 是一种低级的、类汇编的二进制指令格式，设计用于在 Web 浏览器中以接近原生的速度执行代码。

### 核心优势
- **近原生性能**: WASM 字节码比 JavaScript 快 1.5-3 倍（对于计算密集型任务）
- **安全沙箱**: 运行在浏览器的安全沙箱中，无法直接访问系统资源
- **多语言支持**: 可从 C/C++/Rust/Go 等语言编译而来
- **与 JS 互操作**: 可以互相调用函数

### WASM 工作流程
```
C/C++/Rust 源码 → 编译 → .wasm 二进制 → fetch() → WebAssembly.instantiate() → 执行
```

## Java 后端在其中的角色

1. **数据提供者**: 通过 REST API 提供业务数据、配置信息
2. **验证服务**: 提供参考计算结果，验证 WASM 端计算正确性
3. **静态资源服务器**: 托管 WASM 二进制文件和前端页面
4. **业务逻辑层**: 处理数据库、权限、事务等非计算密集型的服务端逻辑

## 接口调用方式

### API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/tasks` | GET | 获取计算任务列表 |
| `/api/testdata/{type}?size={n}` | GET | 获取后端参考计算结果 |
| `/api/wasm-info` | GET | 获取 WASM 模块元信息 |
| `/wasm/fibonacci.wasm` | GET | WASM 二进制文件 |

### 调用示例

```bash
# 获取任务列表
curl http://localhost:8097/api/tasks

# 获取后端参考结果
curl http://localhost:8097/api/testdata/fibonacci?size=40

# 获取 WASM 信息
curl http://localhost:8097/api/wasm-info
```

### 前端调用流程

```javascript
// 1. 从后端获取测试数据
const data = await fetch('/api/testdata/fibonacci?size=40');

// 2. 加载 WASM 模块
const instance = await WebAssembly.instantiateStreaming(
    fetch('/wasm/fibonacci.wasm')
);

// 3. 通过 WASM 执行高性能计算
const result = instance.exports.fibonacci(40);

// 4. 对比后端验证结果
const backendResult = data.fibonacciResult;
```

## 项目结构

```
10-WebAssembly高性能基础/
├── pom.xml                          # Maven 配置 (Spring Boot 3.2.0)
├── README.md
└── src/main/
    ├── java/com/example/wasmdemo/
    │   ├── WasmDemoApplication.java # Spring Boot 启动类
    │   ├── controller/
    │   │   └── CalcController.java  # REST API 控制器
    │   ├── service/
    │   │   └── CalcService.java     # 计算服务
    │   └── entity/
    │       └── CalculationTask.java # 计算任务实体
    └── resources/
        ├── application.properties    # 端口 8097
        └── static/
            ├── index.html            # 前端页面
            └── wasm/
                ├── fibonacci.wasm    # WASM 二进制 (66 bytes)
                ├── fibonacci.wat     # WAT 源码
                └── wasm-simulator.js # WASM 模拟器
```

## 运行方式

```bash
# 确保使用 JDK 21
java -version  # 应显示 21.x

# 运行 Spring Boot 项目
mvn spring-boot:run

# 或直接运行编译后的 jar
mvn package
java -jar target/wasm-demo-1.0.0.jar
```

访问 http://localhost:8097 查看前端页面。

## WASM 模块说明

### fibonacci.wasm
- **功能**: 递归计算斐波那契数列第 n 项
- **导出函数**: `fibonacci: (n: i32) => i32`
- **二进制大小**: 66 字节
- **编译工具**: `wat2wasm` (WebAssembly Binary Toolkit)

### 从 WAT 编译到 WASM
```bash
# 安装 wabt 工具包
npm install -g wabt
# 或使用在线工具: https://webassembly.github.io/wabt/wasm2wat.html

# 编译
wat2wasm fibonacci.wat -o fibonacci.wasm
```

## 性能指标

典型性能对比结果（因设备而异）：

| 计算任务 | 输入规模 | JS 耗时 | WASM 耗时 | 提速比 |
|----------|----------|---------|-----------|--------|
| 斐波那契 | n=40 | ~200ms | ~120ms | ~1.5x |
| 质数筛选 | 100,000 | ~15ms | ~8ms | ~1.8x |
| 图像卷积 | 200x200 | ~45ms | ~25ms | ~1.7x |

> 注意：实际 WASM 优势在 C/Rust 编译的复杂算法中更显著，JS 模拟 WASM 模式下差异较小。

## 浏览器兼容性

- Chrome 57+
- Firefox 52+
- Safari 11+
- Edge 16+

如果不支持 WebAssembly，系统会自动降级到 JavaScript 原生实现。
