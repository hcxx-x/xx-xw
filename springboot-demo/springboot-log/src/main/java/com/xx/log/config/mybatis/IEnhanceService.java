package com.xx.log.config.mybatis;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hanyangyang
 * @since 2023/4/26
 */
public interface IEnhanceService<T> extends IService<T> {

    /**
     * 真正的批量插入
     * 注意这个方法的名称要和定义sql注入器中 创建 InsertBatchSomeColumn 时的入参名称一样，如果没有入参，则使用默认的名称：insertBatchSomeColumn
     * @see com.xx.log.config.mybatis.MySqlInjector#getMethodList(Class, TableInfo)
     * @param list 要批量插入的列表
     * @return
     */
    boolean insertBatchSomeColumn(List<T> list);
}
