package com.xx.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.log.entity.UserInfo;
import com.xx.log.mapper.UserInfoMapper;
import com.xx.log.service.IUserInfoService;
import org.springframework.stereotype.Service;

/**
 * @author hanyangyang
 * @since 2023/3/27
 */
@Service
public class UserInfoImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
