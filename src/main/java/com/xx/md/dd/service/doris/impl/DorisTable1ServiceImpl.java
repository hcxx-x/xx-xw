package com.xx.md.dd.service.doris.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.md.dd.domain.doris.DorisPersonInfo;
import com.xx.md.dd.domain.doris.DorisTable1;
import com.xx.md.dd.mapper.doris.DorisPersonInfoMapper;
import com.xx.md.dd.mapper.doris.DorisTable1Mapper;
import com.xx.md.dd.service.doris.DorisPersonInfoService;
import com.xx.md.dd.service.doris.DorisTable1Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author 28963
* @description 针对表【person_info(OLAP)】的数据库操作Service实现
* @createDate 2024-04-03 09:43:04
*/
@Service
@DS("doris")
public class DorisTable1ServiceImpl extends ServiceImpl<DorisTable1Mapper, DorisTable1>
    implements DorisTable1Service {


}




