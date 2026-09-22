package com.example.esm.entity;

import java.math.BigDecimal;

public class Product {
    private Integer id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private String createdBy;

    public Product() {}
    public Product(Integer id, String name, String category, BigDecimal price, Integer stock, String createdBy) {
        this.id = id; this.name = name; this.category = category; this.price = price; this.stock = stock; this.createdBy = createdBy;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
