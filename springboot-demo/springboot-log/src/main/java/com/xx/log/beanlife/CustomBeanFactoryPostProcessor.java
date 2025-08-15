package com.xx.log.beanlife;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author hanyangyang
 * @since 2025/8/8
 **/
@Slf4j
@Component
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        BeanDefinition beanLifeCicirle = configurableListableBeanFactory.getBeanDefinition("beanLifeCicirle");;
        AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanLifeCicirle;
        int autowireMode = beanDefinition.getAutowireMode();
        log.info("执行BeanFactoryPostProcessor的postProcessBeanFactory的方法, 可以获取BeanDefinition:{},autowiremode:{}",beanLifeCicirle.getBeanClassName(),autowireMode);
    }
}
