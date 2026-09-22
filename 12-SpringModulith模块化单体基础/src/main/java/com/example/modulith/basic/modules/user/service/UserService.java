package com.example.modulith.basic.modules.user.service;

import com.example.modulith.basic.modules.user.api.UserModule;
import com.example.modulith.basic.modules.user.entity.UserEntity;
import com.example.modulith.basic.shared.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 用户模块服务实现
 *
 * 实现UserModule接口（模块对外API），处理用户管理核心逻辑。
 * 使用内存存储模拟数据库。
 */
@Slf4j
@Service
public class UserService implements UserModule {

    private final Map<Long, UserEntity> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        initSampleData();
    }

    /**
     * 初始化示例数据
     */
    private void initSampleData() {
        UserEntity user1 = UserEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("张三")
                .email("zhangsan@example.com")
                .active(true)
                .build();
        UserEntity user2 = UserEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("李四")
                .email("lisi@example.com")
                .active(true)
                .build();
        UserEntity user3 = UserEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name("王五")
                .email("wangwu@example.com")
                .active(false)
                .build();
        userStore.put(user1.getId(), user1);
        userStore.put(user2.getId(), user2);
        userStore.put(user3.getId(), user3);
        log.info("用户模块初始化完成，加载 {} 个用户", userStore.size());
    }

    @Override
    public UserDTO getUser(Long id) {
        UserEntity entity = userStore.get(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    public List<UserDTO> listUsers() {
        return userStore.values().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity entity = UserEntity.builder()
                .id(idGenerator.getAndIncrement())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .active(true)
                .build();
        userStore.put(entity.getId(), entity);
        log.info("创建用户: id={}, name={}", entity.getId(), entity.getName());

        // 发布用户创建事件
        eventPublisher.publishEvent(
                com.example.modulith.basic.shared.event.UserEvent.created(entity.getId(), entity.getName())
        );

        return toDTO(entity);
    }

    @Override
    public void deactivateUser(Long id) {
        UserEntity entity = userStore.get(id);
        if (entity != null && entity.getActive()) {
            entity.setActive(false);
            log.info("停用用户: id={}, name={}", entity.getId(), entity.getName());

            // 发布用户停用事件
            eventPublisher.publishEvent(
                    com.example.modulith.basic.shared.event.UserEvent.deactivated(entity.getId(), entity.getName())
            );
        }
    }

    /**
     * Entity转换为DTO - 内部方法，不对外暴露
     */
    private UserDTO toDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .active(entity.getActive())
                .build();
    }
}
