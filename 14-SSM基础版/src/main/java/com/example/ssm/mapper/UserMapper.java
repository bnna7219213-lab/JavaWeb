package com.example.ssm.mapper;

import com.example.ssm.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User Mapper接口
 * namespace 对应 resources/mapper/UserMapper.xml
 */
public interface UserMapper {

    User selectById(@Param("id") Integer id);

    List<User> selectAll();

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Integer id);

    List<User> selectByName(@Param("name") String name);
}
