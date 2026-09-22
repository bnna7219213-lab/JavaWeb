package com.example.ssm.service.impl;

import com.example.ssm.entity.Order;
import com.example.ssm.exception.BusinessException;
import com.example.ssm.mapper.OrderMapper;
import com.example.ssm.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public Order getById(Integer id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<Order> list(Integer userId, String status, int offset, int limit) {
        return orderMapper.selectList(userId, status, offset, limit);
    }

    @Override
    public long count(Integer userId, String status) {
        return orderMapper.count(userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(Order order) {
        if (order.getOrderNo() == null) order.setOrderNo("ORD" + System.currentTimeMillis());
        if (order.getStatus() == null) order.setStatus("PENDING");
        return orderMapper.insert(order) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Integer id, String status) {
        return orderMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Integer id) {
        return orderMapper.deleteById(id) > 0;
    }

    @Override
    public Map<String, Object> stats() {
        return orderMapper.selectStats();
    }
}
