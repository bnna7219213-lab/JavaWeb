# 10-WebAssembly高性能进阶

## 项目概述

这是 WebAssembly 高性能计算的进阶演示项目。相比基础版，进阶版演示了：
- **多 WASM 模块管理**: 同时加载和管理计算、加密、图像三个独立 WASM 模块
- **真实 WASM 加载器**: 使用 `WebAssembly.instantiateStreaming` 的完整加载器
- **Web Worker 集成**: WASM 在独立 Worker 线程中运行，避免阻塞 UI
- **完整性能对比图表**: 柱状图 + 数据表格展示 JS vs WASM vs Worker 三种模式
- **Spring Boot 后端**: 提供多模块 API、任务列表和参考计算结果

## 架构总览

```
                              HTTP / JSON
                   ┌────────────────────────────┐
                   │                            │
                   ▼                            │
┌──────────────────────────────────────┐        │
│        Spring Boot 后端              │        │
│  Port 8098                           │        │
│  ┌─────────────────────────────────┐ │        │
│  │  /api/v2/modules   模块列表     │─┘        │
│  │  /api/v2/tasks     任务列表     │          │
│  │  /api/v2/compute/*  参考计算    │          │
│  │  /api/v2/worker-*  Worker开销   │          │
│  └─────────────────────────────────┘          │
└──────────────────────────────────────┘          │
                   │          ▲                  │
                   ▼          │                  │
┌──────────────────────────────────────────────┐ │
│            浏览器端                            │ │
│                                              │ │
│  ┌────────────────┐     postMessage          │ │
│  │  主线程         │    ──────────────┐      │ │
│  │  (UI + WASM)   │                  │      │ │
│  │ ┌────────────┐ │   ┌────────────┐ │      │ │
│  │ │WASM Loader │ │   │Web Worker  │ │      │ │
│  │ │            │ │   │(独立线程) │ │      │ │
│  │ │ ┌────────┐│ │   │ ┌────────┐│ │      │ │
│  │ │ │compute ││ │   │ │compute ││ │      │ │
│  │ │ │crypto  ││ │   │ │ 模块    ││ │      │ │
│  │ │ │image   ││ │   │ └────────┘│ │      │ │
│  │ │ └────────┘│ │   └────────────┘ │      │ │
│  │ └────────────┘ │                  │      │ │
│  └────────────────┘──────────────────┘      │ │
└──────────────────────────────────────────────┘ │
```

## WASM 多层执行模型

### 三层执行对比

| 层级 | 执行位置 | WASM 可用 | 阻塞 UI | 适用场景 |
|------|----------|-----------|---------|----------|
| JS 主线程 | 主线程 | 否 | 是 | 轻量计算、DOM 操作 |
| WASM 主线程 | 主线程 | 是 | 是 | 中等计算、快速响应 |
| WASM Worker | Worker 线程 | 是 | 否 | 重度计算、大数据处理 |

### 性能预期

```
轻量计算:  JS ≈ WASM主线程 < WASM Worker (Worker 序列化有额外开销)
中等计算:  JS > WASM主线程 < WASM Worker (WASM 优势显现)
重度计算:  JS >> WASM主线程 < WASM Worker (并行执行避免 UI 冻结)
```

## 多 WASM 模块架构

### 模块职责划分

```
compute-engine.wasm ── 斐波那契 / 质数 / 矩阵 / 排序
crypto-engine.wasm  ── 哈希 / 加密轮函数 / 位运算
image-engine.wasm  ── 像素批量变换 / 内存操作
```

每条 WASM 模块是一个独立编译的 .wasm 二进制文件，包含：
- **导出函数**: JS 可调用的原生函数
- **Memory 对象**: 线性内存空间，可存储原始数据
- **类型签名**: 明确声明参数和返回类型

### 模块通信 (JS <-> WASM)

```javascript
// 1. 加载模块
const instance = await WebAssembly.instantiateStreaming(fetch('compute-engine.wasm'));

// 2. 直接调用导出函数 (值传递)
const result = instance.exports.fibonacci(42);

// 3. 共享内存 (大数据传输)
const memory = instance.exports.memory;  // WebAssembly.Memory
const view = new Uint8Array(memory.buffer);
view.set(dataArray, offset);  // 写入数据
instance.exports.batchTransform(offset, count, factor);  // WASM 处理
const output = view.slice(offset, offset + count * 4);  // 读取结果
```

## Java 后端 API

### 模块管理 API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v2/modules` | GET | 获取所有 WASM 模块信息 |
| `/api/v2/tasks` | GET | 获取计算任务列表 |
| `/api/v2/compute/{type}?size={n}` | GET | 获取参考计算结果 |
| `/api/v2/worker-overhead` | GET | Worker 开销信息 |

### 调用示例

```bash
curl http://localhost:8098/api/v2/modules
curl http://localhost:8098/api/v2/tasks
curl http://localhost:8098/api/v2/compute/fibonacci?size=42
```

## 前端调用流程

```javascript
// 1. 初始化 WASM 加载器
const loader = new WasmModuleLoader();
const detect = loader.detect();

// 2. 并行加载多个模块
const results = await loader.loadMultiple([
    { name: 'compute-engine', url: 'wasm/compute-engine.wasm' },
    { name: 'crypto-engine', url: 'wasm/crypto-engine.wasm' },
    { name: 'image-engine', url: 'wasm/image-engine.wasm' }
]);

// 3. 获取导出函数并调用
const fib = loader.getFunction('compute-engine', 'fibonacci');
const result = fib(42);

// 4. Web Worker 计算
worker.postMessage({ command: 'compute', moduleName: 'compute-engine', funcName: 'fibonacci', args: [42] });
```

## WASM 加载原理

### 标准加载流程

```
fetch('module.wasm')
    │
    ▼
WebAssembly.instantiateStreaming(response, imports)
    │
    ├─ 流式编译 (边下载边编译)
    │
    ▼
{ module, instance }
    │
    ├─ module: 编译后的 WASM 模块 (可重用)
    │
    └─ instance: 实例化模块 (包含导出函数和内存)
        │
        └─ .exports: { fibonacci: f, memory: Memory, ... }
```

### 降级流程 (不支持 streaming)

```
fetch('module.wasm') => ArrayBuffer
    │
    ▼
WebAssembly.compile(bytes) => Module
    │
    ▼
WebAssembly.instantiate(module, imports) => Instance
```

## 项目结构

```
10-WebAssembly高性能进阶/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/wasmadvanced/
    │   ├── WasmAdvancedApplication.java
    │   ├── controller/
    │   │   └── AdvancedCalcController.java
    │   ├── service/
    │   │   └── AdvancedCalcService.java
    │   └── entity/
    │       ├── CalculationTask.java
    │       └── TaskResult.java
    └── resources/
        ├── application.properties              # 端口 8098
        └── static/
            ├── index.html                       # 前端页面
            ├── css/style.css                    # 样式
            └── wasm/
                ├── wasm-loader.js               # WASM 加载器
                ├── wasm-worker.js               # Web Worker 脚本
                ├── compute-engine.wasm          # 计算引擎 (66 bytes)
                ├── compute-engine.wat           # 计算引擎 WAT
                ├── crypto-engine.wasm           # 加密引擎 (41 bytes)
                ├── crypto-engine.wat            # 加密引擎 WAT
                ├── image-engine.wasm            # 图像引擎 (47 bytes)
                └── image-engine.wat             # 图像引擎 WAT
```

## 运行方式

```bash
# 编译运行
mvn spring-boot:run

# 打包
mvn package
java -jar target/wasm-advanced-1.0.0.jar
```

访问 http://localhost:8098 查看前端页面。

## WASM 开发工具

### WAT → WASM 编译
```bash
# 安装 WebAssembly Binary Toolkit
npm install -g wabt

# 编译
wat2wasm compute-engine.wat -o compute-engine.wasm

# 反编译 (调试)
wasm2wat compute-engine.wasm -o debug.wat

# 查看导出
wasm-objdump -x compute-engine.wasm
```

### 在线工具
- **WAT Playground**: https://webassembly.github.io/wabt/demo/wat2wasm/
- **WASM Explorer**: https://mbebenita.github.io/WasmExplorer/

## 性能优化要点

1. **SharedArrayBuffer**: JS 和 WASM 共享内存，零拷贝数据传输
2. **多 Worker**: 多个 Worker + WASM 实现并行计算
3. **内存池**: 预分配 Memory 避免动态扩容开销
4. **批量处理**: 减少 JS ↔ WASM 调用次数，批量传递数据
5. **流式加载**: `instantiateStreaming` 边下载边编译，减少等待时间

## 浏览器兼容性

| 特性 | Chrome | Firefox | Safari | Edge |
|------|--------|---------|--------|------|
| WASM 基础 | 57+ | 52+ | 11+ | 16+ |
| instantiateStreaming | 61+ | 58+ | 11+ | 16+ |
| SharedArrayBuffer | 68+ | 79+ | 15.2+ | 79+ |
| Web Worker | 全部 | 全部 | 全部 | 全部 |
