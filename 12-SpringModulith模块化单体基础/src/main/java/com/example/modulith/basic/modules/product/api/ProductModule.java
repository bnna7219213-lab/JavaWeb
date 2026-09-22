package com.example.modulith.basic.modules.product.api;

import com.example.modulith.basic.shared.dto.ProductDTO;

import java.util.List;

/**
 * ProductModule接口 - 商品模块对外暴露的API
 *
 * 其他模块必须通过此接口访问商品数据，
 * 不能直接引用product模块的内部实现。
 */
public interface ProductModule {

    /**
     * 根据ID获取商品
     */
    ProductDTO getProduct(Long id);

    /**
     * 列出所有商品
     */
    List<ProductDTO> listProducts();

    /**
     * 创建商品
     */
    ProductDTO createProduct(ProductDTO productDTO);

    /**
     * 减少商品库存
     */
    boolean reduceStock(Long productId, int quantity);
}
