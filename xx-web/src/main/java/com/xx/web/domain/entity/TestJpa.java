package com.xx.web.domain.entity;


import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Data
@Entity(name = "test_jpa")
@Table(name = "test_jpa")
public class TestJpa {
    @Id
    @Column(name = "id")
    private Long id;

    /**内容*/
    @Column(name = "content", length = 2, nullable = false)
    @Comment("测试内容")
    private String content;
}
