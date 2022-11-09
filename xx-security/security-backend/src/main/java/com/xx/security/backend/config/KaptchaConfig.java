package com.xx.security.backend.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.google.code.kaptcha.util.ConfigHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @auther: hanyangyang
 * @date: 2022/11/9
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer producer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "158");
        properties.setProperty("kaptcha.image.height", "158");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha producer = new DefaultKaptcha();
        producer.setConfig(config);
        return producer;
    }

}
