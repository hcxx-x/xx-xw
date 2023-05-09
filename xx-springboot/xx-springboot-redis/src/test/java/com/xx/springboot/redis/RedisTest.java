package com.xx.springboot.redis;


import com.xx.springboot.redis.config.RedisUtils;
import com.xx.springboot.redis.config.RedisUtils2;
import com.xx.springboot.redis.pojo.RedisTestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

/**
 * @auther: hanyangyang
 * @date: 2022/11/28
 */
@SpringBootTest
public class RedisTest {

    @Test
    public void test() {
        RedisTestDTO redisTestDTO = new RedisTestDTO();
        redisTestDTO.setId(1L);
        redisTestDTO.setDesc("test");
        redisTestDTO.setName("name-test");
        RedisUtils.lAdd("redis:dto",redisTestDTO);

        List<RedisTestDTO> set = RedisUtils.getList("redis:dto", RedisTestDTO.class);
        set.forEach(item->{
            System.out.println(item.getName());
        });
        System.out.println(set);
    }


}
