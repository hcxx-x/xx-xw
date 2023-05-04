package com.xx.gf.mapper;


import com.gf.config.mp.EnhanceBaseMapper;
import com.xx.gf.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hanyangyang
 * @since 2023/3/27
 */
@Mapper
public interface UserInfoMapper extends EnhanceBaseMapper<UserInfo> {
 List<UserInfo> queryAllUserConditionIdIn(@Param("ids") List<Integer> ids);

 List<Long> testIntLong();
}
