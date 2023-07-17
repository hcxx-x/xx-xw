package com.xx.xw.stmp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.xw.stmp.pojo.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.ResultHandler;

@Mapper
public interface ITestMapper extends BaseMapper<TestEntity> {

}
