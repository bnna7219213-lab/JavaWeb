package com.aether.bff.entity;

import java.io.Serializable;

/**
 * 用户实体
 *
 * <p>BFF层根据设备类型裁剪返回字段：
 * - PC端：返回完整字段（含积分详情、偏好设置等）
 * - 移动端：仅返回核心字段（用户名、头像、等级）</p>
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String email;
    private String avatar;
    private String memberLevel;
    private Integer points;
    private String phone;
    private String bio;

    public User() {
    }

    public User(String id, String username, String email, String avatar,
                String memberLevel, Integer points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.memberLevel = memberLevel;
        this.points = points;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getMemberLevel() { return memberLevel; }
    public void setMemberLevel(String memberLevel) { this.memberLevel = memberLevel; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
