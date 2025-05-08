package com.example.springbootredis.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestDTO  implements Serializable {
    private Long id;
    private String name;
}
