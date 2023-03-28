package com.xx.log.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liu zhao
 * mybatisplus 相关配置
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * sql记录插件与分页插件
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // sql记录插件
        interceptor.addInnerInterceptor(new MyBatisPlusSqlLogInterceptor());
        return interceptor;
    }


   /* @Bean
    @SuppressWarnings("deprecation")
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {configuration.setUseDeprecatedExecutor(false)};
    }*/
}
