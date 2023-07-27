package com.xx.shardingsphere.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.shardingsphere.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hanyangyang
 * @since 2023/7/26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
