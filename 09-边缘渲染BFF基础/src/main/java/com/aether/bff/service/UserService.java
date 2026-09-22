package com.aether.bff.service;

import com.aether.bff.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 用户服务（模拟内部微服务）
 *
 * <p>模拟一个独立的"用户微服务"，提供用户基本信息查询接口。
 * 在真实架构中，这可能是通过Feign/gRPC/HTTP调用的远程服务。</p>
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * 获取当前登录用户信息
     *
     * <p>模拟调用 GET /internal/users/me</p>
     * @return 当前用户信息
     */
    public User getCurrent() {
        log.debug("[UserService] 调用用户微服务 - getCurrentUser()");
        mockNetworkDelay(80);

        return new User(
                "U-10086",
                "张三",
                "zhangsan@example.com",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan",
                "VIP-金卡",
                12580
        );
    }

    /**
     * 获取用户偏好设置
     *
     * <p>模拟调用 GET /internal/users/{id}/preferences</p>
     */
    public UserPreferences getPreferences(String userId) {
        log.debug("[UserService] 调用用户微服务 - getPreferences({})", userId);
        mockNetworkDelay(40);

        UserPreferences pref = new UserPreferences();
        pref.setTheme("dark");
        pref.setLanguage("zh-CN");
        pref.setCurrency("CNY");
        return pref;
    }

    private void mockNetworkDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 用户偏好设置（内部类）
     */
    public static class UserPreferences {
        private String theme;
        private String language;
        private String currency;

        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}
