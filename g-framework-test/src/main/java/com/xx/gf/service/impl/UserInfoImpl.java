package com.xx.gf.service.impl;


import com.gf.config.mp.EnhanceServiceImpl;
import com.xx.gf.entity.UserInfo;
import com.xx.gf.mapper.UserInfoMapper;
import com.xx.gf.service.IUserInfoService;
import org.springframework.stereotype.Service;

/**
 * @author hanyangyang
 * @since 2023/3/27
 */
@Service
public class UserInfoImpl extends EnhanceServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
