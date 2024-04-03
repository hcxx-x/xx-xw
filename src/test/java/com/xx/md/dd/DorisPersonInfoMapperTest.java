package com.xx.md.dd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xx.md.dd.domain.doris.DorisPersonInfo;
import com.xx.md.dd.mapper.doris.DorisPersonInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author hanyangyang
 * @since 2024/4/3
 */
@Slf4j
@SpringBootTest
public class DorisPersonInfoMapperTest {

    @Resource
    private DorisPersonInfoMapper dorisPersonInfoMapper;

    @Test
    public void testInsert() {
        DorisPersonInfo dorisPersonInfo = new DorisPersonInfo();
        dorisPersonInfo.setName("inert");
        dorisPersonInfo.setEmail("234234@qq.com");
        dorisPersonInfoMapper.insert(dorisPersonInfo);
    }

    @Test
    public void testUpdate() {
        DorisPersonInfo dorisPersonInfo = dorisPersonInfoMapper.selectById(1L);
        dorisPersonInfo.setName("updateNameMapper");
        dorisPersonInfoMapper.updateById(dorisPersonInfo);
    }

    @Test
    public void testUpdateWrapper() {
        dorisPersonInfoMapper.update(null,new LambdaUpdateWrapper<DorisPersonInfo>()
                .eq(DorisPersonInfo::getId, 2L)
                .set(DorisPersonInfo::getName, "updateWrapperMapper"));
    }

    @Test
    public void testDeleteByQueryWrapper() {
        dorisPersonInfoMapper.delete(new LambdaQueryWrapper<DorisPersonInfo>()
                .eq(DorisPersonInfo::getId, 1775361885713850370L));
    }

    @Test
    public void testDeleteById() {
        dorisPersonInfoMapper.deleteById(7L);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testTx() {
        dorisPersonInfoMapper.update(null,new LambdaUpdateWrapper<DorisPersonInfo>()
                .eq(DorisPersonInfo::getId, 2L)
                .set(DorisPersonInfo::getName, "updateWrapperMapper111"));

        DorisPersonInfo dorisPersonInfo1 = new DorisPersonInfo();
        dorisPersonInfo1.setName("tx_error_after");
        dorisPersonInfo1.setEmail("234234@qq.com");
        int b=0;
        int a = 1 / b;
        dorisPersonInfoMapper.insert(dorisPersonInfo1);
    }
}
