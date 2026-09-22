package com.example;

import com.example.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/user")
    public Map<String, Object> getUser() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "查询成功");
        result.put("data", new User(1, "张三", 24));
        return result;
    }

    @PostMapping("/user/add")
    public Map<String, Object> addUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("收到新增用户：" + user.getName() + "，年龄：" + user.getAge());
        user.setId(1001);
        result.put("code", 200);
        result.put("msg", "用户添加成功");
        result.put("data", user);
        return result;
    }
}
