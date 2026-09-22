package com.example.ssm.mapper;

import com.example.ssm.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderMapper {

    Order selectById(@Param("id") Integer id);

    List<Order> selectList(@Param("userId") Integer userId,
                           @Param("status") String status,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    Long count(@Param("userId") Integer userId,
               @Param("status") String status);

    int insert(Order order);

    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    int deleteById(@Param("id") Integer id);

    /**
     * 统计：订单总数和总金额
     */
    Map<String, Object> selectStats();
}
