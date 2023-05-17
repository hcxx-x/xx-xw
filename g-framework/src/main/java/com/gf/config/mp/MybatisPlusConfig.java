package com.gf.config.mp;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanyangyang
 * @since 2023/4/27
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
        // sql记录插件,存在大量的动态参数的时候，这个日志输出会影响性能
        //interceptor.addInnerInterceptor(new MyBatisPlusSqlLogInterceptor());
        return interceptor;
    }

    @Bean
    public CustomSqlInjector sqlInjector() {
        return new CustomSqlInjector();
    }
}
