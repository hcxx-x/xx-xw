package com.xx.xw.stmp.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.xw.stmp.mapper.ITestMapper;
import com.xx.xw.stmp.pojo.entity.TestEntity;
import com.xx.xw.stmp.service.ITestService;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestServiceImpl extends ServiceImpl<ITestMapper, TestEntity> implements ITestService {

    @Override
    public void testTx() {
        TestServiceImpl testService = SpringUtil.getApplicationContext().getBean(this.getClass());
        testService.realTx();
    }

    @Transactional(rollbackFor = Exception.class)
    public void realTx(){
        TestEntity testEntity = new TestEntity();
        testEntity.setId(4L);
        testEntity.setContent("测试事务--更新后的结果");
        this.updateById(testEntity);
        int a = 1/0;
        TestEntity testEntity1 = new TestEntity();
        testEntity1.setContent("新插入");
        this.save(testEntity1);
    }
}
