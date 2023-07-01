package com.xx.xw.stmp.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_test")
public class TestEntity {
    @TableId(type=IdType.AUTO)
    private Long id;
    private String content;
}
