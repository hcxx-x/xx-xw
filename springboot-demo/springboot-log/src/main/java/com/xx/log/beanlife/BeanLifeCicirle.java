package com.xx.log.beanlife;

import com.xx.log.entity.UserInfo;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * bean 声明周期示例
 * 一：实例化阶段
 * 1：调用构造方法实例化bean（存在无参构造就调无参构造）
 * 二：属性设置方法
 * 2: 执行属性注入 @Autowire 注入 或者 是set 注入
 * 3:执行各种aware方法
 * 三：初始化前
 * 4：执行beanPostProcessor的postProcesserBeforeInitialization方法
 * 四：执行初始化方法
 * 5:@PostConstruct注解的方法
 * 6:如果实现了InitializingBean方法则调用afterPropertiesSet方法
 * 7:@Bean中指定的initMethod方法｜xml 中指定的init-method方法
 * 8:调用BeanPostProcessor中的postPrecessAfterInitialization方法
 * 五：使用
 * 六：销毁
 * 9:如果实现了DisposableBean则调用 destroy方法
 * 10:调用@Bean或者xml中 指定的销毁方法
 * @author hanyangyang
 * @since 2025/8/7
 **/
//@Component
@Slf4j
@ToString
@Getter
public class BeanLifeCicirle implements BeanNameAware, InitializingBean, DisposableBean {
    private SqlSessionFactory sqlSessionFactory;
    private Integer prop;

    public BeanLifeCicirle(){
        log.info("1:调用构造函数实例化");
    }

    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        log.info("2:属性装配，set 方法注入");
        this.sqlSessionFactory = sqlSessionFactory;
    }


    @Override
    public void setBeanName(String s) {
        log.info("3：执行各类的aware接口定义的方法,prop:{}",prop);
    }

    @PostConstruct
    public void postConstructMethod(){
        log.info("5:执行PostConstruct方法");
        prop = Integer.MAX_VALUE;
    }





    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("6:afterPropertiesSet,prop:{}",prop);
    }

    public void initMethod(){
        log.info("7:执行initMethod 方法");
    }

    @Override
    public void destroy() throws Exception {
        log.info("9:disposableBean的destroy方法");
    }

    public void customDestroyMethod(){
        log.info("10:调用自定义/指定好的customDestroyMethod方法");
    }
}
