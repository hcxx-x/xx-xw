package com.xx.nodb.config;

import com.github.houbb.sensitive.word.api.IWordAllow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @auther: hyy
 * @date: 2022/10/11
 */
@Component
public class MyDdWordAllow implements IWordAllow {
    @Override
    public List<String> allow() {
        return Stream.of("性", "干", "屏蔽").collect(Collectors.toList());
    }
}
