package com.xx.md.dd.mapper.doris;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.md.dd.domain.doris.DorisPersonInfo;
import com.xx.md.dd.domain.doris.DorisTable1;

/**
* @author 28963
* @description 针对表【person_info(OLAP)】的数据库操作Mapper
* @createDate 2024-04-03 09:43:04
* @Entity generator.domain.DorisPersonInfo
*/
@DS("doris")
public interface DorisTable1Mapper extends BaseMapper<DorisTable1> {

}




