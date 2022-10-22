package com.xx.nodb.config;

import com.github.houbb.sensitive.word.api.IWordAllow;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @auther: hyy
 * @date: 2022/10/11
 */
@Configuration
public class SpringSensitiveWordConfig {
    @Resource
    private MyDdWordAllow myDdWordAllow;

    @Resource
    private MyDdWordDeny myDdWordDeny;
    /**
     * 初始化引导类
     * @return 初始化引导类
     * @since 1.0.0
     */
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        IWordDeny wordDeny = WordDenys.chains(WordDenys.system(), myDdWordDeny);
        IWordAllow wordAllow = WordAllows.chains(WordAllows.system(), myDdWordAllow);
        SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
                .wordAllow(wordAllow)
                // 各种其他配置
                .init();

        return sensitiveWordBs;
    }
}
