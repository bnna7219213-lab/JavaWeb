package com.example.modulith.advanced.modules.user.api;

import com.example.modulith.advanced.shared.dto.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * UserModule接口 - 用户模块对外暴露的API
 *
 * 通过 @ApplicationModule 描述模块元信息：
 * - 无依赖（基础模块）
 * - 其他模块通过此接口访问用户数据
 *
 */
public interface UserModule {

    Optional<UserDTO> findUser(Long id);

    List<UserDTO> findAllUsers();

    UserDTO createUser(String name, String email);

    UserDTO deactivateUser(Long id);

    boolean isActive(Long userId);
}
