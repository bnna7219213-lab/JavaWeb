package com.example.ssm.service;

import com.example.ssm.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order getById(Integer id);

    List<Order> list(Integer userId, String status, int offset, int limit);

    long count(Integer userId, String status);

    boolean createOrder(Order order);

    boolean updateStatus(Integer id, String status);

    boolean removeById(Integer id);

    Map<String, Object> stats();
}
