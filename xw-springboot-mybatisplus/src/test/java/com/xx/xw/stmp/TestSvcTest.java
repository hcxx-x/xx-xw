package com.xx.xw.stmp;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.xx.xw.stmp.pojo.entity.TestEntity;
import com.xx.xw.stmp.service.ITestService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;

@SpringBootTest()
public class TestSvcTest {

    @Resource
    private ITestService testService;

    @Test
    public void testTx(){
        testService.testTx();
    }

    @Test
    public void testBatchSave(){
        ArrayList<TestEntity> list = new ArrayList<>();
        for (int i = 0; i < 30000; i++) {
            TestEntity entity = new TestEntity();
            entity.setContent(i+"");
            list.add(entity);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        testService.saveBatch(list);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
