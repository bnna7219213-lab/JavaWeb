package com.aether.bff.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 缓存配置 - 模拟边缘CDN缓存层
 *
 * <p>使用 ConcurrentMapCacheManager 模拟分布式缓存行为。
 * 生产环境中应替换为 Redis / Caffeine 等分布式缓存方案。</p>
 *
 * <p>缓存空间规划：</p>
 * <pre>
 *  user-info     - 用户基础信息缓存（TTL 30min）
 *  dashboard     - Dashboard聚合结果缓存（TTL 5min）
 *  order-detail  - 订单详情数据缓存（TTL 15min）
 *  product-list  - 推荐商品列表缓存（TTL 2min）
 * </pre>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager() {
        log.info("[CacheConfig] 初始化缓存管理器 - 模拟边缘CDN缓存层");

        ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager();
        manager.setCacheNames(List.of(
                "user-info",
                "dashboard",
                "order-detail",
                "product-list"
        ));

        log.info("[CacheConfig] 已配置缓存空间: user-info, dashboard, order-detail, product-list");
        return manager;
    }
}
