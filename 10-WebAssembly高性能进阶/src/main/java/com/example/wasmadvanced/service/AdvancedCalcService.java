package com.example.wasmadvanced.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 高级计算服务 - 模拟多种 WASM 模块的计算功能
 */
@Service
public class AdvancedCalcService {

    /**
     * SHA-256 哈希计算
     */
    public String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 矩阵乘法 - 模拟计算密集型任务
     */
    public double[][] matrixMultiply(double[][] a, double[][] b) {
        int rows = a.length;
        int cols = b[0].length;
        int inner = b.length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double sum = 0;
                for (int k = 0; k < inner; k++) {
                    sum += a[i][k] * b[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    /**
     * 生成随机矩阵
     */
    public double[][] generateRandomMatrix(int size) {
        Random rand = new Random(42);
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rand.nextDouble() * 100;
            }
        }
        return matrix;
    }

    /**
     * 快速排序
     */
    public void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    /**
     * 生成测试数据
     */
    public int[] generateIntArray(int size) {
        Random rand = new Random(42);
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(100000);
        }
        return arr;
    }

    /**
     * 快速傅里叶变换 (简化版 DFT)
     */
    public double[] dft(double[] signal) {
        int n = signal.length;
        double[] real = new double[n];
        double[] imag = new double[n];

        for (int k = 0; k < n; k++) {
            double sumReal = 0, sumImag = 0;
            for (int t = 0; t < n; t++) {
                double angle = 2 * Math.PI * t * k / n;
                sumReal += signal[t] * Math.cos(angle);
                sumImag += -signal[t] * Math.sin(angle);
            }
            real[k] = sumReal;
            imag[k] = sumImag;
        }

        double[] magnitude = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            magnitude[i] = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
        }
        return magnitude;
    }

    /**
     * AdaBoost 模拟 - 简单的机器学习推理
     */
    public double predict(double[] features) {
        double[] weights = {0.8, 1.2, -0.5, 1.1, -0.3, 0.7, -1.0, 0.9, 0.4, -0.6};
        double bias = -0.5;
        double result = bias;
        for (int i = 0; i < Math.min(features.length, weights.length); i++) {
            result += features[i] * weights[i];
        }
        return 1.0 / (1.0 + Math.exp(-result)); // sigmoid
    }
}
