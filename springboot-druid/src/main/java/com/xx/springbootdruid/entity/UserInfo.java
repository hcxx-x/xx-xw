package com.xx.springbootdruid.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author hanyangyang
 * @since 2023/8/29
 */
@TableName("user_info")
@Data
public class UserInfo {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
