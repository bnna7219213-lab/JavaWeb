package com.example.esm.entity;

public class User {
    private Integer id;
    private String name;
    private String email;
    private String department;
    private Integer age;

    public User() {}
    public User(Integer id, String name, String email, String department, Integer age) {
        this.id = id; this.name = name; this.email = email; this.department = department; this.age = age;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
