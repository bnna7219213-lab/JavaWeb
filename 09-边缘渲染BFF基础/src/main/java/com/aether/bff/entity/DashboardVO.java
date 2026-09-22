package com.aether.bff.entity;

import java.util.List;

/**
 * 仪表盘视图对象（BFF聚合结果）
 *
 * <p>这是BFF层的核心产物——将多个微服务的数据聚合成前端页面所需的完整结构。
 * 前端只需一次HTTP请求即可获得整个Dashboard所需的所有数据。</p>
 *
 * <p>对比传统方式：</p>
 * <pre>
 *   传统方式：前端发起4次请求
 *     GET /api/users/me
 *     GET /api/orders/recent
 *     GET /api/recommendations
 *     GET /api/notifications/unread-count
 *
 *   BFF方式：前端只需1次请求
 *     GET /api/bff/dashboard  -> 返回聚合后的DashboardVO
 * </pre>
 */
public class DashboardVO {

    /** 页面标题 - BFF可以针对不同端定制 */
    private String pageTitle;

    /** 用户信息模块 */
    private User user;

    /** 近期订单模块 */
    private List<Order> recentOrders;

    /** 推荐商品模块 */
    private List<Product> recommendations;

    /** 通知未读数 */
    private Integer unreadNotificationCount;

    /** 聚合元信息 */
    private Meta meta;

    public DashboardVO() {
    }

    public DashboardVO(User user, List<Order> recentOrders, List<Product> recommendations) {
        this.pageTitle = "我的首页";
        this.user = user;
        this.recentOrders = recentOrders;
        this.recommendations = recommendations;
        this.unreadNotificationCount = 3;
        this.meta = new Meta();
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Order> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<Order> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public List<Product> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Product> recommendations) {
        this.recommendations = recommendations;
    }

    public Integer getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public void setUnreadNotificationCount(Integer unreadNotificationCount) {
        this.unreadNotificationCount = unreadNotificationCount;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * 聚合元数据 - 用于标识数据来源和版本
     */
    public static class Meta {
        private String bffVersion = "basic-1.0";
        private String dataSources = "user-service,order-service,recommend-service,notification-service";
        private Long generatedAt = System.currentTimeMillis();

        public String getBffVersion() { return bffVersion; }
        public void setBffVersion(String bffVersion) { this.bffVersion = bffVersion; }
        public String getDataSources() { return dataSources; }
        public void setDataSources(String dataSources) { this.dataSources = dataSources; }
        public Long getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(Long generatedAt) { this.generatedAt = generatedAt; }
    }
}
