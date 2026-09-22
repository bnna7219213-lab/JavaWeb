/**
 * 高级 WASM 模块加载器
 * 功能：
 * 1. 自动检测浏览器 WASM 支持
 * 2. 多模块并行加载 (compute-engine, crypto-engine, image-engine)
 * 3. WebAssembly.instantiateStreaming + 降级方案
 * 4. 模块生命周期管理 (load, instantiate, cache, dispose)
 * 5. 性能监控 (加载时间、实例化时间)
 */

class WasmModuleLoader {
    constructor() {
        this.modules = new Map();
        this.loading = new Map();
        this.metrics = new Map();
        this.importObject = {
            env: {
                memory: new WebAssembly.Memory({ initial: 256, maximum: 512 }),
                abort: function(msg, file, line, col) {
                    console.error('WASM abort:', { msg: msg, file: file, line: line, col: col });
                },
                'console.log': function(x) { console.log('WASM log:', x); }
            },
            wasi_snapshot_preview1: {
                proc_exit: function(code) { console.log('WASM exit:', code); },
                fd_write: function() { return 0; }
            }
        };
    }

    /**
     * 检测 WebAssembly 支持情况
     */
    detect() {
        var result = {
            supported: false,
            streaming: false,
            details: {}
        };

        if (typeof WebAssembly === 'undefined') {
            result.details.error = 'WebAssembly global object not found';
            return result;
        }

        try {
            var testModule = new WebAssembly.Module(new Uint8Array([
                0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00
            ]));
            result.supported = testModule instanceof WebAssembly.Module;
        } catch (e) {
            result.details.error = e.message;
            return result;
        }

        result.streaming = typeof WebAssembly.instantiateStreaming === 'function';
        result.details.streamingSupport = result.streaming;

        // 检测 SharedArrayBuffer (用于 Shared Memory)
        try {
            if (typeof SharedArrayBuffer !== 'undefined') {
                new SharedArrayBuffer(1024);
                result.details.sharedArrayBuffer = true;
            }
        } catch (e) {
            result.details.sharedArrayBuffer = false;
        }

        return result;
    }

    /**
     * 加载单个 WASM 模块
     * @param {string} name - 模块名称
     * @param {string} url - .wasm 文件 URL
     * @param {Object} [customImports] - 自定义导入对象
     */
    async loadModule(name, url, customImports) {
        if (this.modules.has(name)) {
            return this.modules.get(name);
        }

        if (this.loading.has(name)) {
            return this.loading.get(name);
        }

        var loadPromise = this._doLoad(name, url, customImports);
        this.loading.set(name, loadPromise);

        try {
            var instance = await loadPromise;
            this.modules.set(name, instance);
            return instance;
        } finally {
            this.loading.delete(name);
        }
    }

    async _doLoad(name, url, customImports) {
        var metrics = { name: name, url: url, startTime: performance.now() };
        var imports = customImports || this.importObject;

        var instance;

        try {
            if (WebAssembly.instantiateStreaming) {
                // 方法1: 流式加载（推荐）
                metrics.method = 'instantiateStreaming';
                var response = await fetch(url);
                if (!response.ok) throw new Error('HTTP ' + response.status);
                var result = await WebAssembly.instantiateStreaming(response, imports);
                instance = result.instance;
            } else {
                // 方法2: 传统加载（兼容性更好）
                metrics.method = 'compile+instantiate';
                var resp = await fetch(url);
                if (!resp.ok) throw new Error('HTTP ' + resp.status);
                var bytes = await resp.arrayBuffer();
                metrics.byteSize = bytes.byteLength;
                var module = await WebAssembly.compile(bytes);
                instance = await WebAssembly.instantiate(module, imports);
            }

            var endTime = performance.now();
            metrics.totalTime = endTime - metrics.startTime;
            metrics.exports = Object.keys(instance.exports);
            metrics.exportsCount = metrics.exports.length;
            this.metrics.set(name, metrics);

            console.log('[WASM Loader] ' + name + ' loaded in ' + metrics.totalTime.toFixed(2) + 'ms');
            return instance;

        } catch (err) {
            var endTime = performance.now();
            metrics.totalTime = endTime - metrics.startTime;
            metrics.error = err.message;
            this.metrics.set(name, metrics);
            console.error('[WASM Loader] Failed to load ' + name + ':', err);
            throw err;
        }
    }

    /**
     * 并行加载多个模块
     */
    async loadMultiple(moduleDefs) {
        var self = this;
        var promises = moduleDefs.map(function(def) {
            return self.loadModule(def.name, def.url, def.imports)
                .then(function(instance) {
                    return { name: def.name, instance: instance, success: true };
                })
                .catch(function(err) {
                    return { name: def.name, error: err.message, success: false };
                });
        });

        return Promise.all(promises);
    }

    /**
     * 获取已加载模块的导出函数
     */
    getFunction(moduleName, funcName) {
        var instance = this.modules.get(moduleName);
        if (!instance) throw new Error('Module ' + moduleName + ' not loaded');
        var fn = instance.exports[funcName];
        if (!fn) throw new Error('Function ' + funcName + ' not found in ' + moduleName);
        return fn;
    }

    /**
     * 获取模块内存对象
     */
    getMemory(moduleName) {
        var instance = this.modules.get(moduleName);
        if (!instance) return null;
        return instance.exports.memory || null;
    }

    /**
     * 分配 WASM 内存中的空间（用于与 JS 共享数据）
     */
    allocateInMemory(moduleName, size) {
        var instance = this.modules.get(moduleName);
        if (!instance) return -1;

        // 使用模块的 malloc 导出（如果有），否则直接操作 memory.buffer
        var malloc = instance.exports.malloc;
        if (malloc) return malloc(size);

        // 降级：使用 memory.buffer 直接操作
        var memory = instance.exports.memory;
        if (memory) {
            var buffer = memory.buffer;
            var view = new Uint8Array(buffer);
            return view.byteLength - size; // 简化：返回 buffer 末尾偏移
        }
        return -1;
    }

    /**
     * 获取模块加载指标
     */
    getMetrics(moduleName) {
        if (moduleName) return this.metrics.get(moduleName);
        return Object.fromEntries(this.metrics);
    }

    /**
     * 列出所有已加载模块
     */
    listModules() {
        return Array.from(this.modules.keys());
    }

    /**
     * 卸载模块（释放内存）
     */
    dispose(moduleName) {
        this.modules.delete(moduleName);
        this.metrics.delete(moduleName);
    }

    disposeAll() {
        this.modules.clear();
        this.metrics.clear();
    }
}

// ===== 预配置模块定义 =====
WasmModuleLoader.PRESET_MODULES = [
    {
        name: 'compute-engine',
        url: 'wasm/compute-engine.wasm',
        description: '通用计算引擎 - 斐波那契、质数筛选、矩阵运算'
    },
    {
        name: 'crypto-engine',
        url: 'wasm/crypto-engine.wasm',
        description: '加密计算引擎 - SHA-256、HMAC 处理'
    },
    {
        name: 'image-engine',
        url: 'wasm/image-engine.wasm',
        description: '图像处理引擎 - 卷积、滤波、像素变换'
    }
];

// ===== JavaScript 降级函数 =====
// 当 WASM 模块不可用时使用，模拟 WASM 计算行为
WasmModuleLoader.createJsFallback = function(moduleName) {
    var libraries = {
        'compute-engine': {
            fibonacci: function(n) {
                if (n <= 1) return n;
                var a = 0, b = 1;
                for (var i = 2; i <= n; i++) { var t = a + b; a = b; b = t; }
                return b;
            },
            add: function(a, b) { return a + b; },
            sumRange: function(start, end) {
                var sum = 0;
                for (var i = start; i < end; i++) sum += i;
                return sum;
            }
        },
        'crypto-engine': {
            process: function(x) { return x; },
            xorRound: function(a, b) { return a ^ b; },
            rotateLeft: function(val, bits) { return ((val << bits) | (val >>> (32 - bits))) >>> 0; }
        },
        'image-engine': {
            transform: function(val, factor) { return val * factor; },
            batchTransform: function(offset, count, factor) { /* no-op in JS fallback */ }
        }
    };
    return libraries[moduleName] || {};
};
