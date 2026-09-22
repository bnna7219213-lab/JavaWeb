package com.example.wasmadvanced.controller;

import com.example.wasmadvanced.service.AdvancedCalcService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 高级计算任务 API Controller
 * 支持多种 WASM 模块类型的计算和数据接口
 */
@RestController
@RequestMapping("/api/v2")
@CrossOrigin(origins = "*")
public class AdvancedCalcController {

    private final AdvancedCalcService calcService;

    public AdvancedCalcController(AdvancedCalcService calcService) {
        this.calcService = calcService;
    }

    /**
     * 获取所有可用 WASM 模块信息
     */
    @GetMapping("/modules")
    public Map<String, Object> getModules() {
        Map<String, Object> data = new LinkedHashMap<>();

        List<Map<String, String>> modules = new ArrayList<>();

        Map<String, String> compute = new LinkedHashMap<>();
        compute.put("name", "compute-engine");
        compute.put("description", "通用计算引擎");
        compute.put("functions", "fibonacci, sieve, matrixMultiply, quickSort, dft");
        compute.put("compute", true);
        modules.add(compute);

        Map<String, String> crypto = new LinkedHashMap<>();
        crypto.put("name", "crypto-engine");
        crypto.put("description", "加密与哈希计算引擎");
        crypto.put("functions", "sha256, hmac, pbkdf2");
        modules.add(crypto);

        Map<String, String> image = new LinkedHashMap<>();
        image.put("name", "image-engine");
        image.put("description", "图像处理引擎");
        image.put("functions", "convolution, sobel, gaussian, median");
        modules.add(image);

        data.put("modules", modules);
        data.put("recommended", List.of(
            Map.of("task", "fibonacci", "module", "compute-engine"),
            Map.of("task", "sha256", "module", "crypto-engine"),
            Map.of("task", "convolution", "module", "image-engine")
        ));
        return data;
    }

    /**
     * 获取任务列表（进阶版）
     */
    @GetMapping("/tasks")
    public List<Map<String, Object>> getTasks() {
        List<Map<String, Object>> tasks = new ArrayList<>();

        Map<String, Object> fib = new LinkedHashMap<>();
        fib.put("type", "fibonacci");
        fib.put("name", "斐波那契数列");
        fib.put("module", "compute-engine");
        fib.put("inputType", "number");
        fib.put("defaultInput", 42);
        fib.put("maxInput", 50);
        tasks.add(fib);

        Map<String, Object> prime = new LinkedHashMap<>();
        prime.put("type", "sieve");
        prime.put("name", "质数筛选");
        prime.put("module", "compute-engine");
        prime.put("inputType", "number");
        prime.put("defaultInput", 500000);
        tasks.add(prime);

        Map<String, Object> matrix = new LinkedHashMap<>();
        matrix.put("type", "matrix");
        matrix.put("name", "矩阵乘法");
        matrix.put("module", "compute-engine");
        matrix.put("inputType", "number");
        matrix.put("defaultInput", 100);
        tasks.add(matrix);

        Map<String, Object> sort = new LinkedHashMap<>();
        sort.put("type", "sort");
        sort.put("name", "快速排序");
        sort.put("module", "compute-engine");
        sort.put("inputType", "number");
        sort.put("defaultInput", 100000);
        tasks.add(sort);

        Map<String, Object> hash = new LinkedHashMap<>();
        hash.put("type", "sha256");
        hash.put("name", "SHA-256 哈希");
        hash.put("module", "crypto-engine");
        hash.put("inputType", "text");
        hash.put("defaultInput", 10000);
        tasks.add(hash);

        Map<String, Object> conv = new LinkedHashMap<>();
        conv.put("type", "convolution");
        conv.put("name", "图像卷积");
        conv.put("module", "image-engine");
        conv.put("inputType", "number");
        conv.put("defaultInput", 300);
        tasks.add(conv);

        return tasks;
    }

    /**
     * 获取后端参考计算结果
     */
    @GetMapping("/compute/{type}")
    public Map<String, Object> compute(@PathVariable String type,
                                        @RequestParam(defaultValue = "100") int size,
                                        @RequestParam(required = false) String data) {
        Map<String, Object> result = new LinkedHashMap<>();
        long start = System.currentTimeMillis();

        try {
            switch (type) {
                case "fibonacci":
                    result.put("value", fibonacci(size));
                    break;
                case "sha256":
                    String input = data != null ? data : "WebAssembly Demo Data x".repeat(Math.max(1, size));
                    result.put("hash", calcService.sha256(input.substring(0, Math.min(input.length(), 1000))));
                    break;
                case "sort":
                    int[] arr = calcService.generateIntArray(size);
                    calcService.quickSort(arr, 0, arr.length - 1);
                    result.put("sortedFirst10", Arrays.copyOf(arr, Math.min(10, arr.length)));
                    break;
                case "matrix":
                    double[][] matA = calcService.generateRandomMatrix(size);
                    double[][] matB = calcService.generateRandomMatrix(size);
                    double[][] matResult = calcService.matrixMultiply(matA, matB);
                    result.put("sum", Arrays.stream(matResult).flatMapToDouble(Arrays::stream).sum());
                    break;
                default:
                    result.put("error", "Unknown type: " + type);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        long duration = System.currentTimeMillis() - start;
        result.put("serverDurationMs", duration);
        result.put("type", type);
        result.put("inputSize", size);
        return result;
    }

    /**
     * Web Worker 开销模拟
     */
    @GetMapping("/worker-overhead")
    public Map<String, Double> getWorkerOverhead() {
        Map<String, Double> result = new LinkedHashMap<>();
        result.put("serializationOverheadMs", 0.5);
        result.put("contextSwitchMs", 0.3);
        result.put("averageTotalMs", 1.2);
        result.put("note", 0.0);
        return result;
    }

    private long fibonacci(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }
}
