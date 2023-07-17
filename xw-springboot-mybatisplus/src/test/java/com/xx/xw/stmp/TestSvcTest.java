package com.xx.xw.stmp;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.xx.xw.stmp.mapper.IExportMapper;
import com.xx.xw.stmp.pojo.entity.ExportEntity;
import com.xx.xw.stmp.pojo.entity.TestEntity;
import com.xx.xw.stmp.service.ITestService;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.swing.text.html.parser.Entity;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest()
public class TestSvcTest {

    @Resource
    private ITestService testService;

    @Resource
    private IExportMapper exportMapper;


    @Test
    public void testStreamQuery(){
        AtomicInteger count = new AtomicInteger(0);
        exportMapper.getAll(new ResultHandler<ExportEntity>() {
            @Override
            public void handleResult(ResultContext<? extends ExportEntity> resultContext) {
                ExportEntity resultObject = resultContext.getResultObject();
                System.out.println(resultObject.toString());
                count.incrementAndGet();
            }
        });
        System.out.println(count.get());
    }

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
