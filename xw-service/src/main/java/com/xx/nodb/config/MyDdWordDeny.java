package com.xx.nodb.config;

import com.github.houbb.sensitive.word.api.IWordDeny;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @auther: hyy
 * @date: 2022/10/11
 */
@Component
public class MyDdWordDeny implements IWordDeny {
    @Override
    public List<String> deny() {
        return null;
    }
}
