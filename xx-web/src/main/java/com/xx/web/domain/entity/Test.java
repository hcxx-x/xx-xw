package com.xx.web.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@TableName("app_test")
public class Test {
    @Id
    @TableId(type = IdType.AUTO)
    private Long id;

    /**内容*/
    @Column(name = "content1", length = 2, nullable = false, columnDefinition = "comment '测试内容'")
    private String content;
}
