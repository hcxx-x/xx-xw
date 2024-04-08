package com.xx.md.dd.service.mysql;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.md.dd.domain.mysql.PersonInfo;

/**
 * @author 28963
 * @description 针对表【person_info】的数据库操作Service
 * @createDate 2024-04-03 11:09:25
 */
public interface PersonInfoService extends IService<PersonInfo> {
    void testTx();
}
