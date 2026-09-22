package com.example.wasmdemo.controller;

import com.example.wasmdemo.service.CalcService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 计算任务 API Controller
 * 提供测试数据和验证服务
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CalcController {

    private final CalcService calcService;

    public CalcController(CalcService calcService) {
        this.calcService = calcService;
    }

    /**
     * 获取计算任务列表
     */
    @GetMapping("/tasks")
    public List<Map<String, Object>> getTasks() {
        List<Map<String, Object>> tasks = new ArrayList<>();

        Map<String, Object> fib = new LinkedHashMap<>();
        fib.put("type", "fibonacci");
        fib.put("name", "斐波那契数列");
        fib.put("description", "计算第n个斐波那契数，递归实现");
        fib.put("defaultInput", 40);
        fib.put("inputMin", 1);
        fib.put("inputMax", 50);
        tasks.add(fib);

        Map<String, Object> prime = new LinkedHashMap<>();
        prime.put("type", "prime");
        prime.put("name", "质数筛选");
        prime.put("description", "埃拉托斯特尼筛法找出所有小于N的质数");
        prime.put("defaultInput", 100000);
        prime.put("inputMin", 100);
        prime.put("inputMax", 1000000);
        tasks.add(prime);

        Map<String, Object> image = new LinkedHashMap<>();
        image.put("type", "image");
        image.put("name", "图像卷积");
        image.put("description", "对测试图像应用高斯模糊卷积");
        image.put("defaultInput", 200);
        image.put("inputMin", 50);
        image.put("inputMax", 1000);
        tasks.add(image);

        return tasks;
    }

    /**
     * 获取测试数据
     */
    @GetMapping("/testdata/{type}")
    public Map<String, Object> getTestData(@PathVariable String type,
                                            @RequestParam(defaultValue = "1000") int size) {
        Map<String, Object> data = new LinkedHashMap<>();
        switch (type) {
            case "fibonacci":
                data.put("fibonacciResult", calcService.fibonacci(size));
                data.put("description", "后端参考结果 (Java 端计算)");
                break;
            case "prime":
                List<Integer> primes = calcService.sieveOfEratosthenes(size);
                data.put("primeCount", primes.size());
                data.put("lastPrimes", primes.subList(Math.max(0, primes.size() - 10), primes.size()));
                data.put("description", "后端参考结果 (Java 端计算)");
                break;
            case "image":
                int[][] image = calcService.generateTestImage(size, size);
                double[][] kernel = {
                    {1.0/16, 2.0/16, 1.0/16},
                    {2.0/16, 4.0/16, 2.0/16},
                    {1.0/16, 2.0/16, 1.0/16}
                };
                int[][] result = calcService.applyConvolution(image, kernel);
                long pixelSum = 0;
                for (int[] row : result) {
                    for (int val : row) {
                        pixelSum += val;
                    }
                }
                data.put("pixelSum", pixelSum);
                data.put("description", "后端参考结果 (Java 端计算)");
                break;
        }
        data.put("type", type);
        data.put("inputSize", size);
        return data;
    }

    /**
     * WASM 模块元数据
     */
    @GetMapping("/wasm-info")
    public Map<String, Object> getWasmInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        List<Map<String, String>> modules = new ArrayList<>();

        Map<String, String> mod1 = new LinkedHashMap<>();
        mod1.put("name", "fibonacci");
        mod1.put("description", "WASM 斐波那契计算模块");
        mod1.put("version", "1.0");
        modules.add(mod1);

        Map<String, String> mod2 = new LinkedHashMap<>();
        mod2.put("name", "prime");
        mod2.put("description", "WASM 质数筛选模块");
        mod2.put("version", "1.0");
        modules.add(mod2);

        Map<String, String> mod3 = new LinkedHashMap<>();
        mod3.put("name", "image");
        mod3.put("description", "WASM 图像处理模块");
        mod3.put("version", "1.0");
        modules.add(mod3);

        info.put("modules", modules);
        info.put("architecture", "Java(Spring Boot) Backend + WASM Frontend");
        return info;
    }
}
