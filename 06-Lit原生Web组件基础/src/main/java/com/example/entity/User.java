package com.example.entity;

public class User {
    private Integer id;
    private String name;
    private Integer age;
    private String email;
    private String avatar;

    public User() {}

    public User(Integer id, String name, Integer age, String email, String avatar) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.avatar = avatar;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
