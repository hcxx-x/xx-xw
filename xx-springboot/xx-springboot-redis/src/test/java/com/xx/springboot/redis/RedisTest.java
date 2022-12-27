package com.xx.springboot.redis;


import com.xx.springboot.redis.config.RedisUtil;
import com.xx.springboot.redis.pojo.RedisTestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @auther: hanyangyang
 * @date: 2022/11/28
 */
@SpringBootTest
public class RedisTest {

@Test
    public void test(){
    System.out.println(RedisUtil.get("text"));
}


}
