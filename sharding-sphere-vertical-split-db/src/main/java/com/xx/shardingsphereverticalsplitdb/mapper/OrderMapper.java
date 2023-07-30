package com.xx.shardingsphereverticalsplitdb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.shardingsphereverticalsplitdb.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hanyangyang
 * @since 2023/7/26
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
