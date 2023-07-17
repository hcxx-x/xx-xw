package com.xx.xw.stmp.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@TableName("t_export")
public class ExportEntity {
    @TableId(type=IdType.AUTO)
    private Long id;
    private String content;
    private String content1;
    private String content2;
    private String content3;
    private String content4;
    private String content5;
    private String content6;
    private String content7;
    private String content8;
    private String content9;
    private String content10;
    private String content11;
    private String content12;
    private String content13;
    private String content14;
    private String content15;
}
