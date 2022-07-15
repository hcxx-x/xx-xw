package com.xx.web.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
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
