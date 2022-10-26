package com.xx.nodb;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @auther: hyy
 * @date: 2022/10/11
 */
@Slf4j
@SpringBootTest()
public class SensitiveWordTest {

    @Resource
    private SensitiveWordBs sensitiveWordBs;

    @Test
    public void test(){
        String text = "吧啦啦吧";
        List<String> all = sensitiveWordBs.findAll(text);
        log.info("所有敏感词：{}",all);
    }
}
