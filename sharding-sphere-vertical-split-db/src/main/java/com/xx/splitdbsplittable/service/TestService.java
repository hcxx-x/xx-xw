package com.xx.splitdbsplittable.service;

import com.xx.splitdbsplittable.entity.Order;
import com.xx.splitdbsplittable.entity.User;
import com.xx.splitdbsplittable.mapper.OrderMapper;
import com.xx.splitdbsplittable.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class TestService {
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;


    @Transactional
    public void testTx(){
        Order order = new Order();
        order.setOrderNo("2");
        orderMapper.insert(order);


        User user = new User();
        user.setUname("张三");
        userMapper.insert(user);

        int a = 1/0;
    }
}
