package com.example.ssm.mapper;

import com.example.ssm.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User selectById(@Param("id") Integer id);

    List<User> selectList(@Param("keyword") String keyword);

    Long count(@Param("keyword") String keyword);

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Integer id);
}
