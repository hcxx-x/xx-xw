package com.xx.xw.stmp;

import com.xx.xw.stmp.service.ITestService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest()
public class TestSvcTest {

    @Resource
    private ITestService testService;

    @Test
    public void testTx(){
        testService.testTx();
    }
}
