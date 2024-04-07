package com.xx.md.dd.service.doris;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.md.dd.domain.doris.DorisPersonInfo;

/**
* @author 28963
* @description 针对表【person_info(OLAP)】的数据库操作Service
* @createDate 2024-04-03 09:43:04
*/
@DS("doris")
public interface DorisPersonInfoService extends IService<DorisPersonInfo> {

}
