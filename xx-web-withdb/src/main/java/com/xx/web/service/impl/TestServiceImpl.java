package com.xx.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.web.domain.entity.mp.TestEntity;
import com.xx.web.mapper.TestMapper;
import com.xx.web.service.TestService;
import org.springframework.stereotype.Service;

/**
 * @author: hyy
 * @date: 2023/2/1
 */
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, TestEntity> implements TestService {
}
