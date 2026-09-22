package com.aether.bff.entity;

/**
 * 商品实体
 *
 * <p>模拟从"商品微服务"获取的商品信息。
 * BFF层会根据前端场景对商品数据进行裁剪和格式化。</p>
 */
public class Product {

    private String id;
    private String name;
    private String image;
    private Double price;
    private Double originalPrice;
    private String category;
    private Double rating;
    private Integer salesCount;
    private String tag;

    public Product() {
    }

    public Product(String id, String name, String image, Double price, Double originalPrice,
                   String category, Double rating, Integer salesCount, String tag) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.originalPrice = originalPrice;
        this.category = category;
        this.rating = rating;
        this.salesCount = salesCount;
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
