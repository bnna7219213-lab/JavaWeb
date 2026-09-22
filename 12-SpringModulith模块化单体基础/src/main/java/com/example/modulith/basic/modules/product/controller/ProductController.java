package com.example.modulith.basic.modules.product.controller;

import com.example.modulith.basic.modules.product.api.ProductModule;
import com.example.modulith.basic.shared.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 商品模块控制器
 *
 * 通过ProductModule接口访问商品服务，遵循依赖倒置原则。
 */
@Slf4j
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductModule productModule;

    public ProductController(ProductModule productModule) {
        this.productModule = productModule;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productModule.listProducts());
        model.addAttribute("pageTitle", "商品管理 - 模块化单体基础版");
        return "product/products";
    }

    @GetMapping("/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        ProductDTO product = productModule.getProduct(id);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "product/product-detail";
    }

    @PostMapping
    public String createProduct(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam BigDecimal price,
                                @RequestParam Integer stock) {
        ProductDTO productDTO = ProductDTO.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .build();
        productModule.createProduct(productDTO);
        return "redirect:/products";
    }
}
