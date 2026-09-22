package com.example.modulith.advanced.modules.product.controller;

import com.example.modulith.advanced.modules.product.api.ProductModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
        model.addAttribute("products", productModule.findAllProducts());
        model.addAttribute("pageTitle", "商品管理 - 模块化单体进阶版");
        return "product/products";
    }

    @PostMapping
    public String createProduct(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam BigDecimal price,
                                @RequestParam Integer stock) {
        productModule.createProduct(name, description, price, stock);
        return "redirect:/products";
    }
}
