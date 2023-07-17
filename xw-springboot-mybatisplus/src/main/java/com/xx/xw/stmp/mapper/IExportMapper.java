package com.xx.xw.stmp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.xw.stmp.pojo.entity.ExportEntity;
import com.xx.xw.stmp.pojo.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;

@Mapper
public interface IExportMapper extends BaseMapper<ExportEntity> {
    void getAll(ResultHandler<ExportEntity> resultHandler);

}
