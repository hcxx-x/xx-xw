package com.xx.gf;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.xx.gf.entity.UserInfo;
import com.xx.gf.service.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@SpringBootTest
class GFrameworkTestApplicationTests {
    @Resource
    private IUserInfoService userInfoService;

    @Test
    void testBatchInsert() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<UserInfo> list = new ArrayList<>();
        for (int a = 0; a < 1; a++) {
            for (int i = 0; i < 2; i++) {
                list.add(UserInfo.builder().name(i+"").age(i).email(i+"@qq.com").build());
            }
            userInfoService.realBatchSave(list);
            List<UserInfo> idIsNullList = list.stream().filter(e -> Objects.isNull(e.getId())).toList();
            if (CollUtil.isNotEmpty(idIsNullList)){
                log.info("id回填失败数据：{}",idIsNullList);
            }
        }

        stopWatch.stop();
        log.info("批量插入耗时：{}ms",stopWatch.getTotalTimeMillis());
    }

}
