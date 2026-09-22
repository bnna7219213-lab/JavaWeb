package com.aether.bff.service;

import com.aether.bff.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户服务（模拟内部微服务）- 进阶版
 *
 * <p>使用 Spring Cache 模拟边缘CDN缓存层：</p>
 * <ul>
 *   <li>{@code @Cacheable} - 命中缓存时直接返回，不执行方法体</li>
 *   <li>{@code @CacheEvict} - 数据更新时清除缓存</li>
 * </ul>
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * 获取当前用户信息
     *
     * <p>{@code @Cacheable(value = "user-info")} 表示首次调用后结果会被缓存，
     * 后续相同参数的调用直接返回缓存值，不执行方法体。</p>
     *
     * <p>模拟CDN边缘缓存：数据在BFF层缓存，减少对内部服务的请求压力。</p>
     */
    @Cacheable(value = "user-info", key = "'current-user'")
    public User getCurrent() {
        log.info("[UserService] 缓存未命中 - 实际调用用户微服务 getCurrentUser()");
        mockNetworkDelay(80);

        User user = new User(
                "U-10086",
                "张三",
                "zhangsan@example.com",
                "https://api.dicebear.com/7.x/avataar/svg?seed=zhangsan",
                "VIP-金卡",
                12580
        );
        user.setPhone("138****5678");
        user.setBio("热爱科技产品的资深用户");
        return user;
    }

    /**
     * 根据ID获取用户（带ID维度的缓存）
     */
    @Cacheable(value = "user-info", key = "#userId")
    public User getById(String userId) {
        log.info("[UserService] 缓存未命中 - 调用用户微服务 getUserById({})", userId);
        mockNetworkDelay(80);

        User user = new User(
                userId, "用户-" + userId, userId + "@example.com", "", "VIP", 0
        );
        return user;
    }

    /**
     * 清除用户缓存
     *
     * <p>当用户数据发生变化时（如修改昵称、充值积分），调用此方法清除缓存。
     * 下次请求时自动刷新数据。</p>
     */
    @CacheEvict(value = "user-info", allEntries = true)
    public void evictUserCache() {
        log.info("[UserService] 清除所有用户缓存");
    }

    private void mockNetworkDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
