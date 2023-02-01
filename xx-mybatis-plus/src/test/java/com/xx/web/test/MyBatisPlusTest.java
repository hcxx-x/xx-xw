package com.xx.web.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xx.web.domain.entity.TestActable;
import com.xx.web.mapper.TestActableMapper;
import com.xx.web.service.TestService;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

@Slf4j
@SpringBootTest()
public class MyBatisPlusTest {

    @Autowired
    TestActableMapper testActableMapper;

    @Test
    public void testSelect(){
        try {
            List<TestActable> testActables = testActableMapper.selectList(new LambdaQueryWrapper<>());
            log.info("查询结果：{}",testActables);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Autowired
    private TestService testService;

    @Test
    public void testTableLogin(){
        testService.removeById(1);
        testService.removeById(2);
        testService.removeById(3);
        testService.removeById(4);
    }
}
