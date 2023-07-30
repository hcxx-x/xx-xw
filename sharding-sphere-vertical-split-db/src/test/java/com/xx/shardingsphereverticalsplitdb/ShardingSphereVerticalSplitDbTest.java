package com.xx.shardingsphereverticalsplitdb;

import com.xx.shardingsphereverticalsplitdb.entity.Order;
import com.xx.shardingsphereverticalsplitdb.entity.User;
import com.xx.shardingsphereverticalsplitdb.mapper.OrderMapper;
import com.xx.shardingsphereverticalsplitdb.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@SpringBootTest
public class ShardingSphereVerticalSplitDbTest {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Test
    public void testSplitDB(){
        Order order = new Order();
        order.setOrderNo("1");
        orderMapper.insert(order);

        User user = new User();
        user.setUname("张三");
        userMapper.insert(user);

    }

    @Test
    public void testQuery(){
        orderMapper.selectList(null);
        userMapper.selectList(null);
    }

    @Test
    @Transactional
    public void testTx(){
        Order order = new Order();
        order.setOrderNo("2");
        orderMapper.insert(order);

        int a = 1/0;
        User user = new User();
        user.setUname("张三");
        userMapper.insert(user);
    }

}
