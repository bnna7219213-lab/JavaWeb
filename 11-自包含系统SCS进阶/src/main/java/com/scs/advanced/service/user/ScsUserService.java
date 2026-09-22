package com.scs.advanced.service.user;

import com.scs.advanced.entity.user.ScsUser;
import com.scs.advanced.entity.user.ScsUser.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *	User SCS 业务服务
 *
 * <p>管理用户的完整生命周期。这是User SCS的核心业务逻辑。</p>
 *
 * <p>数据存储使用前缀 "USER_SCS_DB" 的 ConcurrentHashMap，
 * 模拟每个SCS有独立的数据库。在实际SCS中，这将是一个真实的独立数据库实例。</p>
 */
@Service
public class ScsUserService {

    private static final Logger log = LoggerFactory.getLogger(ScsUserService.class);

    /**
     * User SCS 的独立数据库模拟
     * Key: userId, Value: ScsUser
     * 前缀 "USER_SCS_DB" 表示这是User SCS独有的数据
     */
    private final Map<String, ScsUser> userDatabase = new ConcurrentHashMap<>();

    /**
     * 创建用户
     */
    public ScsUser createUser(String username, String email, String phone) {
        Objects.requireNonNull(username, "用户名不能为空");
        Objects.requireNonNull(email, "邮箱不能为空");

        // 唯一性校验
        boolean emailExists = userDatabase.values().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
        if (emailExists) {
            throw new IllegalArgumentException("邮箱已被注册: " + email);
        }

        boolean usernameExists = userDatabase.values().stream()
                .anyMatch(u -> username.equalsIgnoreCase(u.getUsername()));
        if (usernameExists) {
            throw new IllegalArgumentException("用户名已存在: " + username);
        }

        ScsUser user = new ScsUser(username, email, phone);
        userDatabase.put(user.getUserId(), user);

        log.info("[User SCS] 用户创建成功: userId={}, username={}, email={}",
                user.getUserId(), username, email);
        return user;
    }

    /**
     * 根据ID查询用户
     */
    public Optional<ScsUser> findById(String userId) {
        return Optional.ofNullable(userDatabase.get(userId));
    }

    /**
     * 查询所有用户
     */
    public List<ScsUser> findAll() {
        return userDatabase.values().stream()
                .sorted(Comparator.comparing(ScsUser::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 更新用户信息
     */
    public ScsUser updateUser(String userId, String email, String phone) {
        ScsUser user = userDatabase.get(userId);
        if (user == null) {
            throw new NoSuchElementException("用户不存在: " + userId);
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        if (phone != null && !phone.isBlank()) {
            user.setPhone(phone);
        }
        return user;
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(String userId) {
        ScsUser removed = userDatabase.remove(userId);
        return removed != null;
    }

    /**
     * 变更状态
     */
    public ScsUser changeStatus(String userId, UserStatus newStatus) {
        ScsUser user = userDatabase.get(userId);
        if (user == null) {
            throw new NoSuchElementException("用户不存在: " + userId);
        }
        user.setStatus(newStatus);
        return user;
    }

    /**
     * 获取用户总数
     */
    public long count() {
        return userDatabase.size();
    }

    /**
     * 获取数据库信息
     */
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("databaseName", "USER_SCS_DB (User SCS 专属)");
        info.put("totalRecords", userDatabase.size());
        info.put("storagePrefix", "USER_SCS_DB");
        info.put("isolationLevel", "完全隔离 - 仅User SCS可访问");
        info.put("scsUnit", "User Self-Contained System");
        return info;
    }
}
