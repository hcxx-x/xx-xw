package com.xx.springbootdemo.config;


import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.xx.springbootdemo.config.mybatisplus.InToEqualsInterceptor;
import com.xx.springbootdemo.config.mybatisplus.SqlExecInterceptor;
import com.xx.springbootdemo.config.mybatisplus.SqlPrintInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@ConditionalOnClass({PaginationInnerInterceptor.class, SqlSessionFactoryBean.class})
@Configuration
public class MybatisPlusConfig {

    
    @Bean
    public InToEqualsInterceptor inToEqualsInterceptor(){
        return new InToEqualsInterceptor();
    }
    
    /**
     * 打印sql结果及耗时
     * @return
     */
    @Bean
    public SqlExecInterceptor sqlExecPrintInterceptor(){
        return new SqlExecInterceptor();
    }

    /**
     * 打印实际执行的sql
     * @return
     */
    @Bean
    public SqlPrintInterceptor sqlPrintInterceptor(){
        return new SqlPrintInterceptor();
    }

}
