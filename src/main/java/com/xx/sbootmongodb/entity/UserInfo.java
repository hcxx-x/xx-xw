package com.xx.sbootmongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author hanyangyang
 * @since 2023/12/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    /**
     * 用户名
     */
    private String username;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 学分
     */
    private Integer score;

    /**
     * 创建时间
     */
    private LocalDateTime crtDateTime;
}