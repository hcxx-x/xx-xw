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
    public void test1(){
        System.out.println("test");
        RedisTestDTO redisTestDTO = new RedisTestDTO();
        redisTestDTO.setId(1L);
        redisTestDTO.setDesc("desc");
        redisTestDTO.setName("name");
       /* ArrayList<RedisTestDTO> list = new ArrayList<>();
        list.add(redisTestDTO);*/
        new RedisUtil<RedisTestDTO>().lSet("test",redisTestDTO);
    }

    @Test
    public void testGet(){
        List<RedisTestDTO> test = new RedisUtil<RedisTestDTO>().lGetAll("test");
        System.out.println(test);
    }

    @Test
    public void testRedisFactory(){
        List<RedisTestDTO> test = RedisFactory.<RedisTestDTO>get().lGetAll("test");
        System.out.println(test);
    }
}
