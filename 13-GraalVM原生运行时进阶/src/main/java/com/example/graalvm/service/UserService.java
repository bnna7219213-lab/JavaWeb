package com.example.graalvm.service;

import com.example.graalvm.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User Service - Production-grade with sample data
 */
@Service
public class UserService {

    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        User u1 = new User(1L, "alice", "alice@example.com", 28, "ADMIN");
        u1.setDisplayName("Alice Chen");
        u1.setPhone("+86-138-0000-0001");
        u1.setDateOfBirth(LocalDate.of(1996, 3, 15));
        u1.setAvatarUrl("/static/img/avatar-alice.png");
        userStore.put(1L, u1);

        User u2 = new User(2L, "bob", "bob@example.com", 35, "USER");
        u2.setDisplayName("Bob Wang");
        u2.setPhone("+86-139-0000-0002");
        u2.setDateOfBirth(LocalDate.of(1989, 7, 22));
        userStore.put(2L, u2);

        User u3 = new User(3L, "charlie", "charlie@example.com", 22, "USER");
        u3.setDisplayName("Charlie Li");
        u3.setPhone("+86-137-0000-0003");
        u3.setDateOfBirth(LocalDate.of(2002, 11, 8));
        userStore.put(3L, u3);

        User u4 = new User(4L, "diana", "diana@example.com", 31, "MODERATOR");
        u4.setDisplayName("Diana Zhang");
        u4.setPhone("+86-136-0000-0004");
        u4.setDateOfBirth(LocalDate.of(1993, 5, 30));
        userStore.put(4L, u4);

        idGenerator.set(5);
    }

    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }

    public Optional<User> findByUsername(String username) {
        return userStore.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public User createUser(User user) {
        long id = idGenerator.getAndIncrement();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
        userStore.put(id, user);
        return user;
    }

    public Optional<User> updateUser(Long id, User updated) {
        User existing = userStore.get(id);
        if (existing == null) return Optional.empty();
        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        existing.setDisplayName(updated.getDisplayName());
        existing.setAge(updated.getAge());
        existing.setRole(updated.getRole());
        existing.setPhone(updated.getPhone());
        existing.setUpdatedAt(LocalDateTime.now());
        return Optional.of(existing);
    }

    public boolean deleteUser(Long id) {
        return userStore.remove(id) != null;
    }

    public long count() {
        return userStore.size();
    }
}
