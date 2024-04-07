package com.xx.md.dd;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xx.md.dd.domain.doris.DorisPersonInfo;
import com.xx.md.dd.domain.doris.DorisTable1;
import com.xx.md.dd.mapper.doris.DorisPersonInfoMapper;
import com.xx.md.dd.mapper.doris.DorisTable1Mapper;
import com.xx.md.dd.service.doris.DorisPersonInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hanyangyang
 * @since 2024/4/3
 */
@Slf4j
@SpringBootTest
public class DorisPersonInfoMapperTest {

    @Resource
    private DorisPersonInfoMapper dorisPersonInfoMapper;

    @Resource
    private DorisTable1Mapper dorisTable1Mapper;

    @Resource
    private DorisPersonInfoService dorisPersonInfoService;

    @Test
    public void testInsert() {
        DorisPersonInfo dorisPersonInfo = new DorisPersonInfo();
        dorisPersonInfo.setName("inert");
        dorisPersonInfo.setEmail("234234@qq.com");
        dorisPersonInfoMapper.insert(dorisPersonInfo);
    }

    @Test
    public void testUpdate() {
        DorisPersonInfo dorisPersonInfo = dorisPersonInfoMapper.selectById(1L);
        dorisPersonInfo.setName("updateNameMapper");
        dorisPersonInfoMapper.updateById(dorisPersonInfo);
    }

    @Test
    public void testUpdateWrapper() {
        dorisPersonInfoMapper.update(null,new LambdaUpdateWrapper<DorisPersonInfo>()
                .eq(DorisPersonInfo::getId, 2L)
                .set(DorisPersonInfo::getName, "updateWrapperMapper"));
    }

    @Test
    public void testDeleteByQueryWrapper() {
        dorisPersonInfoMapper.delete(new LambdaQueryWrapper<DorisPersonInfo>()
                .eq(DorisPersonInfo::getId, 1775361885713850370L));
    }

    @Test
    public void testDeleteById() {
        dorisPersonInfoMapper.deleteById(7L);
    }

    @Test
    @DSTransactional
    public void testTx() {
       /* for (int i = 0; i < 5000; i++) {
            DorisPersonInfo dorisPersonInfo = new DorisPersonInfo();
            dorisPersonInfo.setName(i+"name");
            dorisPersonInfo.setEmail(i+"@qq.com");
            dorisPersonInfoMapper.insert(dorisPersonInfo);
        }*/

        List<DorisPersonInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DorisPersonInfo dorisPersonInfo = new DorisPersonInfo();
            dorisPersonInfo.setName(i+"batch");
            dorisPersonInfo.setEmail(i+"batch@qq.com");
           list.add(dorisPersonInfo);
        }
        dorisPersonInfoService.saveBatch(list);


        for (int i = 0; i < 10; i++) {
            DorisTable1 dorisTable1 = new DorisTable1();
            dorisTable1.setSiteid(i);
            dorisTable1.setCitycode(i);
            dorisTable1.setUsername(i+"username");
            dorisTable1.setPv((long) i);
            dorisTable1Mapper.insert(dorisTable1);
        }

        int b=0;
        int a = 1 / b;


    }
}
