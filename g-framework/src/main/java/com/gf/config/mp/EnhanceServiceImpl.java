package com.gf.config.mp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author hanyangyang
 * @since 2023/4/26
 */
@Service
public class EnhanceServiceImpl<M extends EnhanceBaseMapper<T>, T> extends ServiceImpl<M, T> implements IEnhanceService<T> {

    public static final int LIST_SIZE_LIMIT=1;

    @Value("${mybatis.updateOnDuplicateKey.autoKey.check:true}")
    private Boolean autoPkCheckOnDuplicateUpdate;
    @Resource
    protected M baseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void  realBatchSave(Collection<T> entities) {
        if (CollUtil.isEmpty(entities)){
            return;
        }
        List<List<T>> listParts = CollUtil.split(entities, 500);
        for (List<T> listPart : listParts) {
            baseMapper.realBatchSave(listPart);
        }
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateOnDuplicateKey(T t) {
        Assert.notNull(t,"入参不能为空");
        // 防止事务失效
        SpringUtil.getBean(this.getClass()).insertOrUpdateOnDuplicateKey(List.of(t));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateOnDuplicateKey(Collection collection) {
        if (CollUtil.isNotEmpty(collection)){
            if (autoPkCheckOnDuplicateUpdate){
                TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
                if (tableInfo.havePK() && (tableInfo.getIdType() == IdType.AUTO) && collection.size()>1) {
                    throw new RuntimeException("在主键自增环境下，"+CustomSqlInjector.METHOD_NAME_INSERT_OR_UPDATE_ON_DUPLICATE_KEY +
                            "方法的入参列表元素个数不能大于"+LIST_SIZE_LIMIT+
                            "否则会出现主键回填失败等异常，若不关心主键回填问题，可通过springboot配置文件中配置mybatis.updateOnDuplicateKey.autoKey.check:false关闭");
                }
            }
            this.baseMapper.insertOrUpdateOnDuplicateKey(collection);
        }
    }
}
