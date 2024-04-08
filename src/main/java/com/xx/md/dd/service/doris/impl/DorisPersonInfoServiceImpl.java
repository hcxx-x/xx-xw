package com.xx.md.dd.service.doris.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.md.dd.domain.doris.DorisPersonInfo;
import com.xx.md.dd.domain.doris.DorisTable1;
import com.xx.md.dd.domain.mysql.PersonInfo;
import com.xx.md.dd.mapper.doris.DorisPersonInfoMapper;
import com.xx.md.dd.mapper.doris.DorisTable1Mapper;
import com.xx.md.dd.service.doris.DorisPersonInfoService;
import com.xx.md.dd.service.doris.DorisTable1Service;
import com.xx.md.dd.service.mysql.PersonInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* @author 28963
* @description 针对表【person_info(OLAP)】的数据库操作Service实现
* @createDate 2024-04-03 09:43:04
*/
@DS("doris")
@Service
public class DorisPersonInfoServiceImpl extends ServiceImpl<DorisPersonInfoMapper, DorisPersonInfo>
    implements DorisPersonInfoService {

    @Resource
    private DorisTable1Service dorisTable1Service;

    @Resource
    private PersonInfoService personInfoService;


    @Override
    @DSTransactional
    public void testTx() {
        List<DorisPersonInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DorisPersonInfo dorisPersonInfo = new DorisPersonInfo();
            dorisPersonInfo.setName(i+"batch");
            dorisPersonInfo.setEmail(i+"batch@qq.com");
            list.add(dorisPersonInfo);
        }
        this.saveBatch(list);


        for (int i = 0; i < 10; i++) {
            DorisTable1 dorisTable1 = new DorisTable1();
            dorisTable1.setSiteid(i);
            dorisTable1.setCitycode(i);
            dorisTable1.setUsername(i+"username");
            dorisTable1.setPv((long) i);
            dorisTable1Service.save(dorisTable1);
        }

        List<PersonInfo> list1 = personInfoService.list();
        System.out.println(list1.size());
    }

    @Override
    @DS("doris")
    @Transactional()
    public List<DorisPersonInfo> getAll() {
        return this.list();
    }
}




