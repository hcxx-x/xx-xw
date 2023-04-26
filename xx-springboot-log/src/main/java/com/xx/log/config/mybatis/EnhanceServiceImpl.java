package com.xx.log.config.mybatis;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hanyangyang
 * @since 2023/4/26
 */
@Service
public class EnhanceServiceImpl<M extends EnhanceBaseMapper<T>, T> extends ServiceImpl<M, T> implements IEnhanceService<T> {

    @Resource
    protected M baseMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean  insertBatchSomeColumn(List<T> list) {
        boolean result = true;
        List<List<T>> listParts = ListUtil.split(list, 500);
        for (List<T> listPart : listParts) {
            if (!baseMapper.insertBatchSomeColumn(listPart)){
                result=false;
            }
        }
        return result;
    }
}
