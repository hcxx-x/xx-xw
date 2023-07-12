package com.xx.test;

import com.xx.JacksonUtil;

import java.time.LocalDateTime;

/**
 * @author hanyangyang
 * @since 2023/7/12
 */
public class JacksonUtilTest {
    static String jsonStr = "{\"key\":\"value\"}";
    public static void main(String[] args) {
        System.out.println(JacksonUtil.isJsonArray(jsonStr));
    }

}
class Person{
    private Long id;
    private String name;

    private LocalDateTime brithday;
}
