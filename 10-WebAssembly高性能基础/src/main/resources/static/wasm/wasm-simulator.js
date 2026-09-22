/**
 * WASM 模拟器 - 基础版
 * 核心功能：
 * 1. 加载真实 .wasm 二进制模块
 * 2. 封装计算函数调用
 * 3. 提供 JavaScript 降级方案
 * 4. 执行性能对比测试
 */

class WasmSimulator {
    constructor() {
        this.modules = {};
        this.isSupported = typeof WebAssembly !== 'undefined';
        this.loadStatus = {};
    }

    /**
     * 检测浏览器 WASM 支持
     */
    async detectSupport() {
        if (!this.isSupported) {
            return { supported: false, reason: 'WebAssembly API not available' };
        }
        try {
            const module = new WebAssembly.Module(new Uint8Array([
                0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00
            ]));
            if (module instanceof WebAssembly.Module) {
                return { supported: true, reason: 'WebAssembly is supported' };
            }
        } catch (e) {
            return { supported: false, reason: e.message };
        }
        return { supported: false, reason: 'Unknown error' };
    }

    /**
     * 加载 WASM 模块
     */
    async loadModule(name, url) {
        if (!this.isSupported) {
            this.loadStatus[name] = { loaded: false, error: 'WASM not supported' };
            return null;
        }

        try {
            this.loadStatus[name] = { loaded: false, loading: true };

            // 方法1: 使用 instantiateStreaming（更高效）
            let instance;
            if (WebAssembly.instantiateStreaming) {
                instance = await WebAssembly.instantiateStreaming(fetch(url), {});
            } else {
                // 方法2: 降级到 instantiate
                const response = await fetch(url);
                const bytes = await response.arrayBuffer();
                const module = await WebAssembly.compile(bytes);
                instance = await WebAssembly.instantiate(module, {});
            }

            this.modules[name] = instance;
            this.loadStatus[name] = { loaded: true, loading: false, exports: Object.keys(instance.exports) };
            return instance;
        } catch (err) {
            this.loadStatus[name] = { loaded: false, loading: false, error: err.message };
            console.error('Failed to load WASM module ' + name + ':', err);
            return null;
        }
    }

    /**
     * 获取已加载模块的导出函数
     */
    getExport(name, funcName) {
        if (this.modules[name] && this.modules[name].exports[funcName]) {
            return this.modules[name].exports[funcName];
        }
        return null;
    }
}

// ===== JavaScript 降级实现 =====

/**
 * JavaScript 原生斐波那契 - 递归实现
 */
function jsFibonacci(n) {
    if (n <= 1) return n;
    return jsFibonacci(n - 1) + jsFibonacci(n - 2);
}

/**
 * JavaScript 原生质数筛选 - 埃拉托斯特尼筛法
 */
function jsSieveOfEratosthenes(limit) {
    var isComposite = new Uint8Array(limit + 1);
    var primes = [];
    for (var i = 2; i <= limit; i++) {
        if (!isComposite[i]) {
            primes.push(i);
            for (var j = i * i; j <= limit; j += i) {
                isComposite[j] = 1;
            }
        }
    }
    return primes;
}

/**
 * JavaScript 原生图像卷积
 */
function jsImageConvolution(size) {
    var image = new Float64Array(size * size);
    var seed = 42;
    for (var i = 0; i < size * size; i++) {
        seed = (seed * 1103515245 + 12345) & 0x7fffffff;
        image[i] = seed % 256;
    }

    var kernel = [1/16, 2/16, 1/16, 2/16, 4/16, 2/16, 1/16, 2/16, 1/16];
    var result = new Float64Array(size * size);

    for (var y = 0; y < size; y++) {
        for (var x = 0; x < size; x++) {
            var sum = 0;
            for (var ky = -1; ky <= 1; ky++) {
                for (var kx = -1; kx <= 1; kx++) {
                    var py = Math.min(Math.max(y + ky, 0), size - 1);
                    var px = Math.min(Math.max(x + kx, 0), size - 1);
                    sum += image[py * size + px] * kernel[(ky + 1) * 3 + (kx + 1)];
                }
            }
            result[y * size + x] = Math.min(255, Math.max(0, sum));
        }
    }
    return result;
}

// ===== WASM 计算封装 =====

var WasmCalc = {
    /**
     * WASM 风格斐波那契
     */
    fib: function(n) {
        if (window.wasmSim && window.wasmSim.modules.fibonacci) {
            var fibFunc = window.wasmSim.getExport('fibonacci', 'fibonacci');
            if (fibFunc) return fibFunc(n);
        }
        return jsFibonacci(n);
    },

    /**
     * WASM 风格质数筛选
     */
    sieve: function(limit) {
        return jsSieveOfEratosthenes(limit);
    },

    /**
     * WASM 风格图像卷积
     */
    convolve: function(size) {
        return jsImageConvolution(size);
    }
};

// ===== 性能测试工具 =====

class PerformanceBenchmark {
    constructor() {
        this.results = [];
    }

    /**
     * 运行单次基准测试
     */
    runOnce(name, fn) {
        var start = performance.now();
        var result = fn();
        var end = performance.now();
        var duration = end - start;
        return { name: name, result: result, duration: duration };
    }

    /**
     * 运行对比测试 (WASM vs JS)
     */
    runComparison(taskType, input) {
        var results = {
            task: taskType,
            input: input,
            comparisons: []
        };

        var jsResult, wasmResult;

        switch (taskType) {
            case 'fibonacci':
                jsResult = this.runOnce('js', function() { return jsFibonacci(input); });
                wasmResult = this.runOnce('wasm', function() { return WasmCalc.fib(input); });
                break;

            case 'prime':
                jsResult = this.runOnce('js', function() { return jsSieveOfEratosthenes(input).length; });
                wasmResult = this.runOnce('wasm', function() { return WasmCalc.sieve(input).length; });
                break;

            case 'image':
                jsResult = this.runOnce('js', function() {
                    var r = jsImageConvolution(input);
                    var sum = 0;
                    for (var i = 0; i < r.length; i++) sum += r[i];
                    return sum;
                });
                wasmResult = this.runOnce('wasm', function() {
                    var r = WasmCalc.convolve(input);
                    var sum = 0;
                    for (var i = 0; i < r.length; i++) sum += r[i];
                    return sum;
                });
                break;
        }

        results.comparisons.push({
            mode: 'JavaScript',
            duration: jsResult.duration,
            durationMs: jsResult.duration.toFixed(2),
            result: jsResult.result
        });

        results.comparisons.push({
            mode: 'WebAssembly',
            duration: wasmResult.duration,
            durationMs: wasmResult.duration.toFixed(2),
            result: wasmResult.result
        });

        if (wasmResult.duration > 0 && jsResult.duration > 0) {
            var speedup = jsResult.duration / wasmResult.duration;
            results.speedup = speedup.toFixed(2);
            results.faster = speedup > 1 ? 'WASM' : 'JS';
        }

        this.results.push(results);
        return results;
    }

    /**
     * 运行多次取平均值
     */
    runMultiple(taskType, input, iterations) {
        iterations = iterations || 5;
        var self = this;
        var allResults = [];
        for (var i = 0; i < iterations; i++) {
            allResults.push(this.runComparison(taskType, input));
        }

        var wasmAvg = allResults.reduce(function(s, r) { return s + r.comparisons[1].duration; }, 0) / iterations;
        var jsAvg = allResults.reduce(function(s, r) { return s + r.comparisons[0].duration; }, 0) / iterations;

        return {
            task: taskType,
            input: input,
            iterations: iterations,
            wasmAvg: wasmAvg,
            jsAvg: jsAvg,
            speedup: (jsAvg / wasmAvg).toFixed(2),
            allResults: allResults
        };
    }

    getResults() {
        return this.results;
    }

    clear() {
        this.results = [];
    }
}
