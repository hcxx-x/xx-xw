package com.xx.md.dd.service.mysql.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.md.dd.domain.mysql.PersonInfo;
import com.xx.md.dd.service.mysql.PersonInfoService;

import com.xx.md.dd.mapper.mysql.PersonInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author 28963
* @description 针对表【person_info】的数据库操作Service实现
* @createDate 2024-04-03 11:09:25
*/
@DS("mysql")
@Service
public class PersonInfoServiceImpl extends ServiceImpl<PersonInfoMapper, PersonInfo>
    implements PersonInfoService {

}




