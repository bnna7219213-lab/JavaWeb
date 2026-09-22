package com.example.modulith.basic.modules.product.service;

import com.example.modulith.basic.modules.product.api.ProductModule;
import com.example.modulith.basic.modules.product.entity.ProductEntity;
import com.example.modulith.basic.shared.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 商品模块服务实现
 *
 * 实现ProductModule接口，处理商品管理核心逻辑。
 * 使用内存存储模拟数据库。
 */
@Slf4j
@Service
public class ProductService implements ProductModule {

    private final Map<Long, ProductEntity> productStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ProductService() {
        initSampleData();
    }

    private void initSampleData() {
        ProductEntity p1 = ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("MacBook Pro M3")
                .description("Apple M3 芯片，14英寸 Liquid Retina XDR 显示屏")
                .price(new java.math.BigDecimal("14999.00"))
                .stock(50)
                .build();
        ProductEntity p2 = ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("ThinkPad X1 Carbon")
                .description("13代 i7，32GB RAM，1TB SSD，14英寸 2.8K OLED")
                .price(new java.math.BigDecimal("12999.00"))
                .stock(30)
                .build();
        ProductEntity p3 = ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("Dell XPS 15")
                .description("13代 i9，32GB RAM，1TB SSD，15.6英寸 3.5K OLED")
                .price(new java.math.BigDecimal("16999.00"))
                .stock(20)
                .build();
        productStore.put(p1.getId(), p1);
        productStore.put(p2.getId(), p2);
        productStore.put(p3.getId(), p3);
        log.info("商品模块初始化完成，加载 {} 个商品", productStore.size());
    }

    @Override
    public ProductDTO getProduct(Long id) {
        ProductEntity entity = productStore.get(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    public List<ProductDTO> listProducts() {
        return productStore.values().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        ProductEntity entity = ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .build();
        productStore.put(entity.getId(), entity);
        log.info("创建商品: id={}, name={}", entity.getId(), entity.getName());
        return toDTO(entity);
    }

    @Override
    public boolean reduceStock(Long productId, int quantity) {
        ProductEntity entity = productStore.get(productId);
        if (entity == null || entity.getStock() < quantity) {
            log.warn("库存不足: productId={}, 请求={}, 可用={}", productId, quantity,
                    entity != null ? entity.getStock() : 0);
            return false;
        }
        entity.setStock(entity.getStock() - quantity);
        log.info("减少库存: productId={}, 减少{}, 剩余{}", productId, quantity, entity.getStock());
        return true;
    }

    /**
     * Entity转换为DTO
     */
    private ProductDTO toDTO(ProductEntity entity) {
        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .build();
    }
}
