package com.xx.md.dd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xx.md.dd.domain.doris.DorisPersonInfo;
import com.xx.md.dd.service.doris.DorisPersonInfoService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author hanyangyang
 * @since 2024/4/3
 */
@Slf4j
@SpringBootTest
public class DorisPersonInfoServiceTest {

    @Resource
    private DorisPersonInfoService dorisPersonInfoService;

    @Test
    public void testInsert() {
        DorisPersonInfo dorisPersonInfo = new DorisPersonInfo();
        dorisPersonInfo.setName("inert");
        dorisPersonInfo.setEmail("234234@qq.com");
        dorisPersonInfoService.save(dorisPersonInfo);
    }

    @Test
    public void testUpdate() {
        DorisPersonInfo dorisPersonInfo = dorisPersonInfoService.getById(1L);
        dorisPersonInfo.setName("updateName");
        dorisPersonInfoService.updateById(dorisPersonInfo);
    }

    @Test
    public void testUpdateWrapper() {
        dorisPersonInfoService.update(new LambdaUpdateWrapper<DorisPersonInfo>()
                .eq(DorisPersonInfo::getId, 2L)
                .set(DorisPersonInfo::getName, "updateWrapper"));
    }

    @Test
    public void testDeleteByQueryWrapper() {
        dorisPersonInfoService.remove(new LambdaQueryWrapper<DorisPersonInfo>()
                .eq(DorisPersonInfo::getId, 1775340365591457794L));
    }

    @Test
    public void testDeleteById() {
        dorisPersonInfoService.removeById(6L);
    }

    @Test
    public void testTx() {
        DorisPersonInfo dorisPersonInfo = new DorisPersonInfo();
        dorisPersonInfo.setName("tx_error_before");
        dorisPersonInfo.setEmail("234234@qq.com");
        dorisPersonInfoService.save(dorisPersonInfo);

        DorisPersonInfo dorisPersonInfo1 = new DorisPersonInfo();
        dorisPersonInfo1.setName("tx_error_after");
        dorisPersonInfo1.setEmail("234234@qq.com");
        int a = 1 / 0;
        dorisPersonInfoService.save(dorisPersonInfo1);
    }
}
