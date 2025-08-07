package com.xx.log.beanlife;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author hanyangyang
 * @since 2025/8/7
 **/
@Component
@Slf4j
public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BeanLifeCicirle){
            log.info("8:执行BeanPostProcessor的postProcessAfterInitialization方法,prop:{}",((BeanLifeCicirle) bean).getProp());
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BeanLifeCicirle){
                log.info("4:执行BeanPostProcessor的postProcessBeforeInitialization方法,prop:{}",((BeanLifeCicirle) bean).getProp());
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
