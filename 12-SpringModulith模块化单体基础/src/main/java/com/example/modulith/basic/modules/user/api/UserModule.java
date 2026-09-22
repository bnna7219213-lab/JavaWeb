package com.example.modulith.basic.modules.user.api;

import com.example.modulith.basic.shared.dto.UserDTO;

import java.util.List;

/**
 * UserModule接口 - 用户模块对外暴露的API
 *
 * 其他模块只能通过此接口访问用户模块的功能，
 * 不允许直接引用user模块的内部实现类（entity, internal service等）。
 *
 * 这是模块化设计的核心：通过接口实现模块间解耦（依赖倒置原则）。
 */
public interface UserModule {

    /**
     * 根据ID获取用户
     */
    UserDTO getUser(Long id);

    /**
     * 列出所有用户
     */
    List<UserDTO> listUsers();

    /**
     * 创建用户
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * 停用用户
     */
    void deactivateUser(Long id);
}
