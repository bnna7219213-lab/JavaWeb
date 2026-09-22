package com.example.ssm.service;

import com.example.ssm.entity.User;

import java.util.List;

public interface UserService {

    User getById(Integer id);

    List<User> list(String keyword);

    long count(String keyword);

    boolean addUser(User user);

    boolean updateUser(User user);

    boolean removeById(Integer id);
}
