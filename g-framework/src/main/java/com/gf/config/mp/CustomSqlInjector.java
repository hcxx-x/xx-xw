package com.gf.config.mp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.injector.methods.Upsert;
import com.gf.config.mp.methods.InsertOrUpdateOnDuplicateKey;

import java.util.List;

/**
 * @author hanyangyang
 * @since 2023/4/26
 */
public class CustomSqlInjector extends DefaultSqlInjector {
    /**
     * 定义方法名称
     * 方法作用：真正批量插入的方法名称
     */
    private static final String METHOD_NAME_REAL_BATCH_SAVE="realBatchSave";

    /**
     * 定义方法名称
     * 方法作用：更新数据，不忽略为空的字段
     */
    private static final String METHOD_NAME_UPDATE_NO_IGNORE_NULL="updateUnIgnoreById";


    /**
     * 定义方法名称
     * 方法作用：插入或者更新数据，根据唯一索引判断，如果存在索引冲突就更新，否则就插入
     */
    public static final String METHOD_NAME_INSERT_OR_UPDATE_ON_DUPLICATE_KEY="insertOrUpdateOnDuplicateKey";

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        //添加真正的批量插入方法，方法名为METHOD_NAME_REAL_BATCH_SAVE定义的值，并且在批量查询的时候忽略更新时自动填充的字段
        methodList.add(new InsertBatchSomeColumn(METHOD_NAME_REAL_BATCH_SAVE,i -> i.getFieldFill() != FieldFill.UPDATE));
        //更新时自动填充的字段，不用插入值
        methodList.add(new AlwaysUpdateSomeColumnById(METHOD_NAME_UPDATE_NO_IGNORE_NULL,i -> i.getFieldFill() != FieldFill.UPDATE));
        methodList.add(new InsertOrUpdateOnDuplicateKey(METHOD_NAME_INSERT_OR_UPDATE_ON_DUPLICATE_KEY));
        return methodList;
    }
}

