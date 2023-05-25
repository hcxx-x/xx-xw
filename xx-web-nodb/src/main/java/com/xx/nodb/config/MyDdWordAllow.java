package com.xx.nodb.config;

import com.github.houbb.sensitive.word.api.IWordAllow;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.lang.model.element.VariableElement;
import java.io.*;
import java.net.URL;
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
        ArrayList<String> allowList = new ArrayList<>();
        try (   InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive_words/allow.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
           String context = bufferedReader.readLine();
            while (StringUtils.hasLength(context)){
                allowList.add(context);
                context = bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allowList;
    }
}
