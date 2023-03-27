package com.xx.log;

import com.xx.log.service.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
class XxSpringbootLogApplicationTests {
    @Resource
    private IUserInfoService userInfoService;

    @Test
    void contextLoads() {
        log.info("用户列表：{}",userInfoService.list());
    }

}
