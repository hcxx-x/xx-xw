package com.xx.md.dd.service.doris.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.md.dd.domain.doris.DorisPersonInfo;
import com.xx.md.dd.mapper.doris.DorisPersonInfoMapper;
import com.xx.md.dd.service.doris.DorisPersonInfoService;
import org.springframework.stereotype.Service;

/**
* @author 28963
* @description 针对表【person_info(OLAP)】的数据库操作Service实现
* @createDate 2024-04-03 09:43:04
*/
@Service
@DS("doris")
public class DorisPersonInfoServiceImpl extends ServiceImpl<DorisPersonInfoMapper, DorisPersonInfo>
    implements DorisPersonInfoService {

}




