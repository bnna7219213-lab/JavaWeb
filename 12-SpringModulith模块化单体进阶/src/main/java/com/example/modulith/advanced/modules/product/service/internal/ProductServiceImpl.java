package com.example.modulith.advanced.modules.product.service.internal;

import com.example.modulith.advanced.modules.product.api.ProductModule;
import com.example.modulith.advanced.modules.product.entity.ProductEntity;
import com.example.modulith.advanced.shared.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 商品模块服务实现 - 位于 internal 包内
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductModule {

    private final Map<Long, ProductEntity> productStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ProductServiceImpl() {
        initSampleData();
    }

    private void initSampleData() {
        save(ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("MacBook Pro M3")
                .description("Apple M3 芯片，14英寸 Liquid Retina XDR 显示屏")
                .price(new BigDecimal("14999.00"))
                .stock(50)
                .createdAt(LocalDateTime.now())
                .build());
        save(ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("ThinkPad X1 Carbon")
                .description("13代 i7，32GB RAM，1TB SSD，14英寸 2.8K OLED")
                .price(new BigDecimal("12999.00"))
                .stock(30)
                .createdAt(LocalDateTime.now())
                .build());
        save(ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("Dell XPS 15")
                .description("13代 i9，32GB RAM，1TB SSD，15.6英寸 3.5K OLED")
                .price(new BigDecimal("16999.00"))
                .stock(20)
                .createdAt(LocalDateTime.now())
                .build());
        save(ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("华为 MateBook X Pro")
                .description("13代 i7，32GB RAM，2.5K 触控全面屏")
                .price(new BigDecimal("10999.00"))
                .stock(40)
                .createdAt(LocalDateTime.now())
                .build());
        log.info("[Product] 模块初始化完成，加载 {} 个商品", productStore.size());
    }

    @Override
    public Optional<ProductDTO> findProduct(Long id) {
        return Optional.ofNullable(productStore.get(id)).map(this::toDTO);
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        return productStore.values().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO createProduct(String name, String description, BigDecimal price, Integer stock) {
        ProductEntity entity = ProductEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .createdAt(LocalDateTime.now())
                .build();
        save(entity);
        log.info("[Product] 创建商品: id={}, name={}", entity.getId(), entity.getName());
        return toDTO(entity);
    }

    @Override
    public boolean reduceStock(Long productId, int quantity) {
        ProductEntity entity = productStore.get(productId);
        if (entity == null || entity.getStock() < quantity) {
            log.warn("[Product] 库存不足: productId={}, 请求={}, 可用={}", productId, quantity,
                    entity != null ? entity.getStock() : 0);
            return false;
        }
        entity.setStock(entity.getStock() - quantity);
        log.info("[Product] 减少库存: productId={}, 减少{}, 剩余{}", productId, quantity, entity.getStock());
        return true;
    }

    private void save(ProductEntity entity) {
        productStore.put(entity.getId(), entity);
    }

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
