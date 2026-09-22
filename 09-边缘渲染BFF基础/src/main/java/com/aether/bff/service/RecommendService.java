package com.aether.bff.service;

import com.aether.bff.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 推荐服务（模拟内部微服务）
 *
 * <p>模拟一个独立的"推荐微服务"，基于用户画像提供个性化推荐。
 * 真实场景中可能涉及复杂的推荐算法和实时特征计算。</p>
 */
@Service
public class RecommendService {

    private static final Logger log = LoggerFactory.getLogger(RecommendService.class);

    /**
     * 获取用户个性化推荐商品
     *
     * <p>模拟调用 GET /internal/recommendations?userId={id}&limit=6</p>
     * @param userId 用户ID
     * @return 推荐商品列表
     */
    public List<Product> forUser(String userId) {
        log.debug("[RecommendService] 调用推荐微服务 - getRecommendations({})", userId);
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

    private void mockNetworkDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
