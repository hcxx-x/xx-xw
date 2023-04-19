package com.xx.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.log.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hanyangyang
 * @since 2023/3/27
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
 List<UserInfo> queryAllUserConditionIdIn(@Param("ids") List<Integer> ids);

 List<Long> testIntLong();
}
