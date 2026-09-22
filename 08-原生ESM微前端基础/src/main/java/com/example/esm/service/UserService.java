package com.example.esm.service;

import com.example.esm.entity.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int nextId = 4;

    public UserService() {
        users.put(1, new User(1, "Alice", "alice@example.com", 28));
        users.put(2, new User(2, "Bob", "bob@example.com", 34));
        users.put(3, new User(3, "Charlie", "charlie@example.com", 22));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    public User save(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> update(Integer id, User user) {
        if (users.containsKey(id)) {
            user.setId(id);
            users.put(id, user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public boolean delete(Integer id) {
        return users.remove(id) != null;
    }
}
