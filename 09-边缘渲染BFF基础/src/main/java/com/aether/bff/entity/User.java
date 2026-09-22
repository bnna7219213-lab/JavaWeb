package com.aether.bff.entity;

/**
 * 用户实体
 *
 * <p>模拟从"用户微服务"获取的用户基本信息。
 * 在BFF层中，User通常被裁剪为前端需要的最小字段集。</p>
 */
public class User {

    private String id;
    private String username;
    private String email;
    private String avatar;
    private String memberLevel;
    private Integer points;

    public User() {
    }

    public User(String id, String username, String email, String avatar, String memberLevel, Integer points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.memberLevel = memberLevel;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
