package com.xx.shardingsphereverticalsplitdb.service;

import com.xx.shardingsphereverticalsplitdb.entity.Order;
import com.xx.shardingsphereverticalsplitdb.entity.User;
import com.xx.shardingsphereverticalsplitdb.mapper.OrderMapper;
import com.xx.shardingsphereverticalsplitdb.mapper.UserMapper;
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
