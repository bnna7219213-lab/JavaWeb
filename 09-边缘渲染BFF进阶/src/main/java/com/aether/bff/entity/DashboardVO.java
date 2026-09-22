package com.aether.bff.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 仪表盘视图对象（BFF层级聚合结果）
 *
 * <p>进阶版支持多端差异化数据：
 * - PC端：完整6个推荐商品 + 详细订单信息 + 侧边栏导航
 * - 移动端：精简3个推荐商品 + 折叠式快捷入口</p>
 */
public class DashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页面标题 - BFF可以针对不同端定制 */
    private String pageTitle;

    /** 设备标识：pc / mobile */
    private String deviceType;

    /** 用户信息模块 */
    private User user;

    /** 近期订单模块 */
    private List<Order> recentOrders;

    /** 推荐商品模块 */
    private List<Product> recommendations;

    /** 快捷入口（移动端特有） */
    private List<QuickAction> quickActions;

    /** 通知未读数 */
    private Integer unreadNotificationCount;

    /** 聚合元信息 */
    private Meta meta;

    public DashboardVO() {
        this.meta = new Meta();
    }

    public DashboardVO(User user, List<Order> recentOrders, List<Product> recommendations, String deviceType) {
        this.pageTitle = "pc".equals(deviceType) ? "PC端首页" : "移动端首页";
        this.deviceType = deviceType;
        this.user = user;
        this.recentOrders = recentOrders;
        this.recommendations = recommendations;
        this.unreadNotificationCount = 3;
        this.meta = new Meta();
        // 移动端快捷入口
        if ("mobile".equals(deviceType)) {
            this.quickActions = List.of(
                new QuickAction("📦", "我的订单", "/orders"),
                new QuickAction("⭐", "我的收藏", "/favorites"),
                new QuickAction("💬", "客服中心", "/support"),
                new QuickAction("🎫", "优惠券", "/coupons")
            );
        }
    }

    public String getPageTitle() { return pageTitle; }
    public void setPageTitle(String pageTitle) { this.pageTitle = pageTitle; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Order> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<Order> recentOrders) { this.recentOrders = recentOrders; }
    public List<Product> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Product> recommendations) { this.recommendations = recommendations; }
    public List<QuickAction> getQuickActions() { return quickActions; }
    public void setQuickActions(List<QuickAction> quickActions) { this.quickActions = quickActions; }
    public Integer getUnreadNotificationCount() { return unreadNotificationCount; }
    public void setUnreadNotificationCount(Integer unreadNotificationCount) { this.unreadNotificationCount = unreadNotificationCount; }
    public Meta getMeta() { return meta; }
    public void setMeta(Meta meta) { this.meta = meta; }

    /**
     * 快捷入口（移动端BFF特有）
     */
    public static class QuickAction implements Serializable {
        private String icon;
        private String label;
        private String link;

        public QuickAction() { }

        public QuickAction(String icon, String label, String link) {
            this.icon = icon;
            this.label = label;
            this.link = link;
        }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }

    /**
     * 聚合元数据
     */
    public static class Meta implements Serializable {
        private String bffVersion = "advanced-1.0";
        private String dataSources = "user-service,order-service,recommend-service,notification-service";
        private Long generatedAt = System.currentTimeMillis();
        private Boolean cached = false;

        public String getBffVersion() { return bffVersion; }
        public void setBffVersion(String bffVersion) { this.bffVersion = bffVersion; }
        public String getDataSources() { return dataSources; }
        public void setDataSources(String dataSources) { this.dataSources = dataSources; }
        public Long getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(Long generatedAt) { this.generatedAt = generatedAt; }
        public Boolean getCached() { return cached; }
        public void setCached(Boolean cached) { this.cached = cached; }
    }
}
