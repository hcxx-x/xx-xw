package com.xx.web.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xx.web.mapper.TestMapper;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

@Slf4j
@SpringBootTest()
public class MyBatisPlusTest {

    @Autowired
    TestMapper testMapper;

    @Test
    public void testSelect(){
        try {
            List<com.xx.web.domain.entity.Test> list = testMapper.selectList(new LambdaQueryWrapper<>());
            log.info("查询结果：{}",list);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
