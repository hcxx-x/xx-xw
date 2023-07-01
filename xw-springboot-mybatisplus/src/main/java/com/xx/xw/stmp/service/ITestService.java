package com.xx.xw.stmp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.xw.stmp.pojo.entity.TestEntity;

public interface ITestService extends IService<TestEntity> {
    void testTx();
}
