package com.xx.md.dd.service.mysql.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.md.dd.domain.doris.DorisPersonInfo;
import com.xx.md.dd.domain.mysql.PersonInfo;
import com.xx.md.dd.service.doris.DorisPersonInfoService;
import com.xx.md.dd.service.mysql.PersonInfoService;

import com.xx.md.dd.mapper.mysql.PersonInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 28963
* @description 针对表【person_info】的数据库操作Service实现
* @createDate 2024-04-03 11:09:25
*/
@DS("mysql")
@Service
public class PersonInfoServiceImpl extends ServiceImpl<PersonInfoMapper, PersonInfo>
    implements PersonInfoService {

    @Resource
    private DorisPersonInfoService dorisPersonInfoService;
    @Override
    public void testTx() {
        for (int i = 0; i < 10; i++) {
            PersonInfo personInfo = new PersonInfo();
            personInfo.setAge(i);
            personInfo.setName("name"+i);
            personInfo.setEmail("email"+i);
            personInfo.setPhone("phone"+i);
            this.save(personInfo);
        }
        List<DorisPersonInfo> list = dorisPersonInfoService.getAll();
        System.out.println(list.size());

    }
}




