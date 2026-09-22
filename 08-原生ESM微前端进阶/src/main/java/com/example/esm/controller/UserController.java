package com.example.esm.controller;

import com.example.esm.entity.User;
import com.example.esm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> list(@RequestParam(required = false) String department) {
        if (department != null && !department.isEmpty()) {
            return userService.findByDepartment(department);
        }
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Integer id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Integer id, @RequestBody User user) {
        return userService.update(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return userService.delete(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/stats/departments")
    public Map<String, Object> departmentStats() {
        List<User> all = userService.findAll();
        java.util.Map<String, Long> stats = new java.util.LinkedHashMap<>();
        for (User u : all) {
            stats.merge(u.getDepartment(), 1L, Long::sum);
        }
        return Map.of("departments", stats, "total", (long) all.size());
    }
}
