package com.xx.splitdbsplittable.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.splitdbsplittable.entity.Order;
import com.xx.splitdbsplittable.entity.User;
import com.xx.splitdbsplittable.mapper.OrderMapper;
import com.xx.splitdbsplittable.mapper.UserMapper;
import com.xx.splitdbsplittable.service.IOrderService;
import com.xx.splitdbsplittable.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
}
