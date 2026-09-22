package com.example.ssm.service;

import com.example.ssm.entity.User;

import java.util.List;

public interface UserService {

    User getById(Integer id);

    List<User> listAll();

    boolean addUser(User user);

    boolean updateUser(User user);

    boolean removeById(Integer id);

    List<User> searchByName(String name);
}
