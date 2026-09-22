package com.example.wasmdemo.service;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 计算服务 - 提供测试数据模拟和验证
 * 在真实项目中由 Java 后端（Spring Boot）提供业务数据和 API，
 * 浏览器端通过 WASM 执行计算密集型逻辑。
 */
@Service
public class CalcService {

    /**
     * 生成指定大小的测试数据集
     */
    public int[] generateTestData(int size) {
        Random rand = new Random(42);
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = rand.nextInt(10000) + 1;
        }
        return data;
    }

    /**
     * 后端端斐波那契计算（用于验证前端 WASM 结果）
     */
    public long fibonacci(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    /**
     * 后端端质数筛选
     */
    public List<Integer> sieveOfEratosthenes(int limit) {
        boolean[] isComposite = new boolean[limit + 1];
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (!isComposite[i]) {
                primes.add(i);
                for (long j = (long) i * i; j <= limit; j += i) {
                    isComposite[(int) j] = true;
                }
            }
        }
        return primes;
    }

    /**
     * 模拟图像处理 - 卷积运算
     */
    public int[][] applyConvolution(int[][] image, double[][] kernel) {
        int height = image.length;
        int width = image[0].length;
        int kSize = kernel.length;
        int kHalf = kSize / 2;
        int[][] result = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sum = 0;
                for (int ky = 0; ky < kSize; ky++) {
                    for (int kx = 0; kx < kSize; kx++) {
                        int py = Math.min(Math.max(y + ky - kHalf, 0), height - 1);
                        int px = Math.min(Math.max(x + kx - kHalf, 0), width - 1);
                        sum += image[py][px] * kernel[ky][kx];
                    }
                }
                result[y][x] = (int) Math.min(255, Math.max(0, sum));
            }
        }
        return result;
    }

    public int[][] generateTestImage(int width, int height) {
        Random rand = new Random(42);
        int[][] image = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image[y][x] = rand.nextInt(256);
            }
        }
        return image;
    }
}
