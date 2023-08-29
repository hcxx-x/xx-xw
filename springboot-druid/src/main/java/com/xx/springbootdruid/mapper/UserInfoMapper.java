package com.xx.springbootdruid.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.springbootdruid.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hanyangyang
 * @since 2023/8/29
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
