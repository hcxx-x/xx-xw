package com.xx.log;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xx.log.entity.UserInfo;
import com.xx.log.mapper.UserInfoMapper;
import com.xx.log.service.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class XxSpringbootLogApplicationTests {
    @Resource
    private IUserInfoService userInfoService;

    @Resource
    private UserInfoMapper userInfoMapper;


    @Test
    void contextLoads() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            ids.add(i);
        }
        //System.out.println(ids.stream().map(e -> e.toString()).collect(Collectors.joining(",")));
        // log.info("y用户列表1：{}",userInfoService.list(Wrappers.<UserInfo>lambdaQuery().in(UserInfo::getId,ids)));
        log.info("用户列表：{}",userInfoMapper.queryAllUserConditionIdIn(ids));
    }

}
