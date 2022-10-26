package com.xx.nodb.config;

import com.github.houbb.sensitive.word.api.IWordDeny;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther: hyy
 * @date: 2022/10/11
 */
@Component
public class MyDdWordDeny implements IWordDeny {
    @Override
    public List<String> deny() {
        ArrayList<String> allowList = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream("build/resources/main/sensitive_words/deny.txt");
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
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
