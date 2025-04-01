package com.xx.springbootdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.springbootdemo.entity.User;
import com.xx.springbootdemo.mapper.UserMapper;
import com.xx.springbootdemo.service.IUserService;
import org.springframework.stereotype.Service;


/**
 * @author hanyangyang
 * @date 2025/3/4
 **/
@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements IUserService{

}
