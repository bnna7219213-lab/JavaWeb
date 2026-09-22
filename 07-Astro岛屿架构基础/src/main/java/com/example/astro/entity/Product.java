package com.example.astro.entity;

import java.math.BigDecimal;

/**
 * 产品实体类
 */
public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean available;

    public Product() {
    }

    public Product(Long id, String name, String description, BigDecimal price, String category, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean active) { this.available = available; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + ", category='" + category + "'}";
    }
}
