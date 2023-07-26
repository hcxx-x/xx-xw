package com.xx.xw.stmp.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xx.xw.stmp.mapper.IExportMapper;
import com.xx.xw.stmp.pojo.entity.ExportEntity;
import com.xx.xw.stmp.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private ITestService testService;

    @Resource
    private IExportMapper exportMapper;

    @RequestMapping("/tx")
    public String testTx(){
        testService.testTx();
        return "ok";
    }

    @RequestMapping("/batch")
    public void testBatch(){
        testService.testBatchFail();
    }

    @RequestMapping("/export")
    public void testExport(HttpServletResponse response) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + "filename" + ".xlsx");
        ExcelWriter write = EasyExcel.write(response.getOutputStream(), ExportEntity.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("sheet").build();
        ArrayList<ExportEntity> list = new ArrayList<>();
        exportMapper.getAll(new ResultHandler<List<ExportEntity>>() {
            @Override
            public void handleResult(ResultContext<? extends List<ExportEntity>> resultContext) {
                List<ExportEntity> resultObject = resultContext.getResultObject();
                /*list.add(resultObject);
                write.write(list,writeSheet);
                list.clear();*/
                System.out.println(resultObject.size());
            }
        });
        //write.finish();
        stopWatch.stop();
        log.info("流式查询导出总耗时：{}ms",stopWatch.getTotalTimeMillis());
    }

    @RequestMapping("/export2")
    public void testExport2(HttpServletResponse response) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + "filename" + ".xlsx");
        ExcelWriter write = EasyExcel.write(response.getOutputStream(), ExportEntity.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("sheet").build();
        List<ExportEntity> list = exportMapper.selectList(new LambdaQueryWrapper<>());
       /* write.write(list,writeSheet);
        write.finish();*/
        stopWatch.stop();
        log.info("流式查询导出总耗时：{}ms",stopWatch.getTotalTimeMillis());
    }

}
