package com.xx.splitdbsplittable.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.splitdbsplittable.entity.User;
import com.xx.splitdbsplittable.mapper.UserMapper;
import com.xx.splitdbsplittable.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
}
