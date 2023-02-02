package com.xx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.web.domain.entity.TestActable;
import com.xx.web.domain.entity.mp.TestEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TestMapper extends BaseMapper<TestEntity> {

    @Select("select * from test where id=#{id}")
    TestEntity getById(@Param("id") Long id);
}
