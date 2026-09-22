package com.example.modulith.advanced.modules.product.api;

import com.example.modulith.advanced.shared.dto.ProductDTO;

import java.util.List;
import java.util.Optional;

/**
 * ProductModule接口 - 商品模块对外暴露的API
 *
 * 无依赖（基础模块），上游模块（order）通过此接口引用。
 */
public interface ProductModule {

    Optional<ProductDTO> findProduct(Long id);

    List<ProductDTO> findAllProducts();

    ProductDTO createProduct(String name, String description, java.math.BigDecimal price, Integer stock);

    boolean reduceStock(Long productId, int quantity);
}
