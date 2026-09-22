package com.example.graalvm.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * 注意: 在GraalVM Native Image中，所有需要反射访问的类
 * 必须在RuntimeHints中注册。参见 BasicRuntimeHintsRegistrar。
 *
 * Native Image要求:
 * - 如果需要使用反射(如Jackson序列化)，需要注册MemberCategory
 * - 如果需要JSON序列化，需要提供默认构造器和getter/setter
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private Integer age;
    private String role;
    private LocalDateTime createdAt;

    public User() {
        // 默认构造器 - GraalVM Native Image需要
    }

    public User(Long id, String username, String email, Integer age, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email + "', age=" + age + ", role='" + role + "'}";
    }
}
