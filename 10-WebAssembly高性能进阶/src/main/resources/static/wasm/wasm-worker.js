/**
 * WebAssembly Worker
 * 在 Web Worker 线程中加载和执行 WASM 模块的计算任务
 *
 * 优势：
 * 1. 不阻塞主线程 UI
 * 2. 可并行处理多个计算任务
 * 3. WASM 在 Worker 中执行，避免主线程卡顿
 *
 * 通信协议:
 * 主线程 → Worker: { id, command: 'load'|'compute'|'benchmark', payload }
 * Worker → 主线程: { id, status: 'success'|'error', result, metrics }
 */

// Worker 内部的 WASM 模块缓存
var wasmModules = {};
var wasmSupported = typeof WebAssembly !== 'undefined';

/**
 * Worker 内部 WASM 加载器
 */
async function loadModuleInWorker(name, url) {
    try {
        var instance;
        if (WebAssembly.instantiateStreaming) {
            var response = await fetch(url);
            var result = await WebAssembly.instantiateStreaming(response, {});
            instance = result.instance;
        } else {
            var resp = await fetch(url);
            var bytes = await resp.arrayBuffer();
            var module = await WebAssembly.compile(bytes);
            instance = await WebAssembly.instantiate(module, {});
        }
        wasmModules[name] = instance;
        return { success: true, name: name, exports: Object.keys(instance.exports) };
    } catch (err) {
        return { success: false, name: name, error: err.message };
    }
}

/**
 * 在 Worker 中执行 WASM 计算
 */
async function computeInWorker(moduleName, funcName, args) {
    if (!wasmSupported) {
        return { error: 'WebAssembly not supported in worker' };
    }

    var instance = wasmModules[moduleName];
    if (!instance) {
        return { error: 'Module ' + moduleName + ' not loaded in worker' };
    }

    var fn = instance.exports[funcName];
    if (!fn) {
        return { error: 'Function ' + funcName + ' not found' };
    }

    var start = performance.now();
    var result = fn.apply(null, args);
    var end = performance.now();

    return {
        result: result,
        duration: end - start,
        mode: 'worker+asm'
    };
}

/**
 * 完整的性能基准测试
 */
async function runBenchmarkInWorker(moduleName, funcName, args, jsFallback) {
    var results = {};

    // 1. WASM in Worker
    var wasmResult = await computeInWorker(moduleName, funcName, args);
    results.wasm = wasmResult;

    // 2. JS in Worker (通过 Function 构造)
    var jsStart = performance.now();
    var jsResult = jsFallback ? jsFallback.apply(null, args) : null;
    var jsEnd = performance.now();
    results.js = {
        result: jsResult,
        duration: jsEnd - jsStart,
        mode: 'worker+js'
    };

    // 3. 计算性能比
    if (wasmResult.duration && results.js.duration) {
        results.speedup = results.js.duration / wasmResult.duration;
    }

    return results;
}

/**
 * 简单 SHA-256 模拟 (Worker 端)
 */
function simpleHash(str) {
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
        var chr = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0;
    }
    return Math.abs(hash).toString(16);
}

/**
 * 像素卷积处理 (Worker 端批量处理)
 */
function batchConvolution(data, width, height) {
    var result = new Float32Array(data.length);
    for (var y = 0; y < height; y++) {
        for (var x = 0; x < width; x++) {
            var idx = y * width + x;
            // 模拟简单的 box blur
            var sum = 0, count = 0;
            for (var dy = -1; dy <= 1; dy++) {
                for (var dx = -1; dx <= 1; dx++) {
                    var ny = y + dy, nx = x + dx;
                    if (ny >= 0 && ny < height && nx >= 0 && nx < width) {
                        sum += data[ny * width + nx];
                        count++;
                    }
                }
            }
            result[idx] = sum / count;
        }
    }
    return result;
}

/**
 * Worker 消息处理
 */
self.onmessage = async function(e) {
    var msg = e.data;
    var id = msg.id;
    var command = msg.command;

    try {
        switch (command) {
            case 'load':
                var loadResult = await loadModuleInWorker(msg.moduleName, msg.url);
                self.postMessage({ id: id, status: 'success', command: 'load', result: loadResult });
                break;

            case 'compute':
                var compResult = await computeInWorker(msg.moduleName, msg.funcName, msg.args || []);
                self.postMessage({ id: id, status: 'success', command: 'compute', result: compResult });
                break;

            case 'benchmark':
                var benchResult = await runBenchmarkInWorker(
                    msg.moduleName, msg.funcName, msg.args || [], msg.jsFallback
                );
                self.postMessage({ id: id, status: 'success', command: 'benchmark', result: benchResult });
                break;

            case 'batchConvolution':
                var startTime = performance.now();
                var convResult = batchConvolution(msg.data, msg.width, msg.height);
                var endTime = performance.now();
                self.postMessage({
                    id: id,
                    status: 'success',
                    result: { data: convResult, duration: endTime - startTime }
                });
                break;

            case 'hash':
                var startTime2 = performance.now();
                var hashResult = simpleHash(msg.data || '');
                var endTime2 = performance.now();
                self.postMessage({
                    id: id,
                    status: 'success',
                    result: { hash: hashResult, duration: endTime2 - startTime2 }
                });
                break;

            default:
                self.postMessage({ id: id, status: 'error', error: 'Unknown command: ' + command });
        }
    } catch (err) {
        self.postMessage({ id: id, status: 'error', error: err.message, stack: err.stack });
    }
};

// 通知主线程 Worker 已就绪
self.postMessage({ id: 'init', status: 'ready', wasmSupported: wasmSupported });
