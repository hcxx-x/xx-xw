package com.xx.web.domain.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "test_jpa")
public class TestJpa {
    @Id
    private Long id;

    /**内容*/
    @Column(name = "content", length = 2, nullable = false, columnDefinition = "comment '测试内容'")
    private String content;
}
