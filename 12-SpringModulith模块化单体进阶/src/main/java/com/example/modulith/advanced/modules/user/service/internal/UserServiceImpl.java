package com.example.modulith.advanced.modules.user.service.internal;

import com.example.modulith.advanced.modules.user.api.UserModule;
import com.example.modulith.advanced.modules.user.entity.UserEntity;
import com.example.modulith.advanced.shared.dto.UserDTO;
import com.example.modulith.advanced.shared.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 用户模块服务实现 - 位于 internal 包内
 * 
 * 注意包名为 .internal，表明这是模块内部实现，不允许外部模块直接引用。
 * 必须通过 UserModule 接口访问。
 */
@Slf4j
@Service
public class UserServiceImpl implements UserModule {

    private final Map<Long, UserEntity> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        initSampleData();
    }

    private void initSampleData() {
        save(UserEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("张三")
                .email("zhangsan@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build());
        save(UserEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("李四")
                .email("lisi@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build());
        save(UserEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("王五")
                .email("wangwu@example.com")
                .active(false)
                .createdAt(LocalDateTime.now())
                .build());
        log.info("[User] 模块初始化完成，加载 {} 个用户", userStore.size());
    }

    @Override
    public Optional<UserDTO> findUser(Long id) {
        return Optional.ofNullable(userStore.get(id)).map(this::toDTO);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userStore.values().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(String name, String email) {
        UserEntity entity = UserEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name(name)
                .email(email)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        save(entity);
        log.info("[User] 创建用户: id={}, name={}", entity.getId(), entity.getName());

        // 发布用户创建事件 - notification和audit模块会监听此事件
        eventPublisher.publishEvent(UserEvent.created(entity.getId(), entity.getName()));
        return toDTO(entity);
    }

    @Override
    public UserDTO deactivateUser(Long id) {
        UserEntity entity = userStore.get(id);
        if (entity == null) {
            throw new IllegalArgumentException("用户不存在: " + id);
        }
        entity.setActive(false);
        log.info("[User] 停用用户: id={}, name={}", entity.getId(), entity.getName());

        // 发布用户停用事件
        eventPublisher.publishEvent(UserEvent.deactivated(entity.getId(), entity.getName()));
        return toDTO(entity);
    }

    @Override
    public boolean isActive(Long userId) {
        UserEntity entity = userStore.get(userId);
        return entity != null && entity.getActive();
    }

    private void save(UserEntity entity) {
        userStore.put(entity.getId(), entity);
    }

    private UserDTO toDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .active(entity.getActive())
                .build();
    }
}
