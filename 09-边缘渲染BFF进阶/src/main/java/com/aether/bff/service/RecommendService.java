package com.aether.bff.service;

import com.aether.bff.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 推荐服务（模拟内部微服务）- 进阶版
 *
 * <p>推荐结果缓存1-5分钟，平衡新鲜度与后端压力。</p>
 */
@Service
public class RecommendService {

    private static final Logger log = LoggerFactory.getLogger(RecommendService.class);

    /**
     * PC端推荐：返回6个完整推荐商品
     */
    @Cacheable(value = "product-list", key = "'recommend-pc'")
    public List<Product> forPc() {
        log.info("[RecommendService] 缓存未命中 - 调用推荐微服务 getRecommendations(PC端, limit=6)");
        mockNetworkDelay(200);

        return Arrays.asList(
                new Product("R-001", "智能手表Pro", "⌚", 1999.00, 2499.00,
                        "数码", 4.8, 12580, "限时优惠"),
                new Product("R-002", "蓝牙音箱", "🔊", 399.00, 599.00,
                        "数码", 4.6, 8520, "热销"),
                new Product("R-003", "护眼台灯", "💡", 159.00, 239.00,
                        "家居", 4.7, 3420, "新品"),
                new Product("R-004", "保温杯", "🥤", 89.00, 129.00,
                        "生活", 4.5, 6780, ""),
                new Product("R-005", "运动手环", "📿", 199.00, 299.00,
                        "数码", 4.4, 5690, "推荐"),
                new Product("R-006", "无线充电器", "🔋", 79.00, 129.00,
                        "数码", 4.3, 4210, "")
        );
    }

    /**
     * 移动端推荐：返回3个精简推荐商品
     */
    @Cacheable(value = "product-list", key = "'recommend-mobile'")
    public List<Product> forMobile() {
        log.info("[RecommendService] 缓存未命中 - 调用推荐微服务 getRecommendations(移动端, limit=3)");
        mockNetworkDelay(150);

        return Arrays.asList(
                new Product("R-001", "智能手表Pro", "⌚", 1999.00, 2499.00,
                        "数码", 4.8, 12580, "限时优惠"),
                new Product("R-004", "保温杯", "🥤", 89.00, 129.00,
                        "生活", 4.5, 6780, ""),
                new Product("R-006", "无线充电器", "🔋", 79.00, 129.00,
                        "数码", 4.3, 4210, "")
        );
    }

    private void mockNetworkDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
