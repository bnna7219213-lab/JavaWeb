package com.example.ssm.service.impl;

import com.example.ssm.entity.User;
import com.example.ssm.exception.BusinessException;
import com.example.ssm.mapper.UserMapper;
import com.example.ssm.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> list(String keyword) {
        return userMapper.selectList(keyword);
    }

    @Override
    public long count(String keyword) {
        return userMapper.count(keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new BusinessException(400, "姓名不能为空");
        }
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        if (user.getId() == null) throw new BusinessException(400, "缺少ID");
        return userMapper.update(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Integer id) {
        return userMapper.deleteById(id) > 0;
    }
}
