package com.example.springbootredis.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OtherDTO implements Serializable {
    private Long num;
    private String name;
}
