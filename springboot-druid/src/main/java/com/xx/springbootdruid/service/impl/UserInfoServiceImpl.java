package com.xx.springbootdruid.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.springbootdruid.entity.UserInfo;
import com.xx.springbootdruid.mapper.UserInfoMapper;
import com.xx.springbootdruid.service.IUserInfoService;
import org.springframework.stereotype.Service;

/**
 * @author hanyangyang
 * @since 2023/8/29
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {
}
