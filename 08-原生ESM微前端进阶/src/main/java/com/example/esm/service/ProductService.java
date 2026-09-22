package com.example.esm.service;

import com.example.esm.entity.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductService {
    private final Map<Integer, Product> products = new LinkedHashMap<>();
    private int nextId = 7;

    public ProductService() {
        products.put(1, new Product(1, "Spring Boot 实战", "图书", new BigDecimal("79.00"), 200, "Alice"));
        products.put(2, new Product(2, "机械键盘", "电子", new BigDecimal("399.00"), 50, "Bob"));
        products.put(3, new Product(3, "无线鼠标", "电子", new BigDecimal("129.00"), 120, "Alice"));
        products.put(4, new Product(4, "Type-C 数据线", "配件", new BigDecimal("29.00"), 500, "Charlie"));
        products.put(5, new Product(5, "微前端架构指南", "图书", new BigDecimal("99.00"), 150, "Diana"));
        products.put(6, new Product(6, "降噪耳机", "电子", new BigDecimal("899.00"), 30, "Bob"));
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public Optional<Product> findById(Integer id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product save(Product product) {
        product.setId(nextId++);
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> update(Integer id, Product product) {
        if (products.containsKey(id)) {
            product.setId(id);
            products.put(id, product);
            return Optional.of(product);
        }
        return Optional.empty();
    }

    public boolean delete(Integer id) {
        return products.remove(id) != null;
    }

    public List<Product> findByCategory(String category) {
        List<Product> result = new ArrayList<>();
        for (Product p : products.values()) {
            if (category.equals(p.getCategory())) {
                result.add(p);
            }
        }
        return result;
    }

    public Map<String, Long> categoryStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        for (Product p : products.values()) {
            stats.merge(p.getCategory(), 1L, Long::sum);
        }
        return stats;
    }
}
