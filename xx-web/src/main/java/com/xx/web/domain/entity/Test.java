package com.xx.web.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("app_test")
public class Test {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
}
