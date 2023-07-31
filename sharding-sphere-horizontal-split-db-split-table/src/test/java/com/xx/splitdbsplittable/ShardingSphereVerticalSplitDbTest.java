package com.xx.splitdbsplittable;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xx.splitdbsplittable.entity.Order;
import com.xx.splitdbsplittable.entity.User;
import com.xx.splitdbsplittable.mapper.OrderMapper;
import com.xx.splitdbsplittable.mapper.UserMapper;
import com.xx.splitdbsplittable.service.IOrderService;
import com.xx.splitdbsplittable.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class ShardingSphereVerticalSplitDbTest {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private IUserService userService;

    @Resource
    private IOrderService orderService;

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
    public  void testBatchInsert(){
        List userList = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            User user = new User();
            user.setUname(UUID.randomUUID().toString());
            userList.add(user);
        }

        List orderList = new ArrayList();
        for (int i = 0; i < 16; i++) {
            Order order = new Order();
            order.setOrderNo(UUID.randomUUID().toString());
            orderList.add(order);
        }
        userService.saveBatch(userList);
        orderService.saveBatch(orderList);
    }


    @Test
    public void testOrderBy(){
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>().orderByDesc(Order::getId).last("limit 2"));
        log.info("排序结果：{}",list);
    }

    @Test
    public void testGroupBy(){
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>().groupBy(Order::getId).orderByDesc(Order::getId).last("limit 2"));
        log.info("排序结果：{}",list);
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
