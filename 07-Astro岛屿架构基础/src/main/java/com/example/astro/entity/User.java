package com.example.astro.entity;

/**
 * 用户实体类
 */
public class User {

    private Long id;
    private String name;
    private String email;
    private String avatar;
    private String role;
    private boolean active;

    public User() {
    }

    public User(Long id, String name, String email, String avatar, String role, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.role = role;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', role='" + role + "', active=" + active + "}";
    }
}
