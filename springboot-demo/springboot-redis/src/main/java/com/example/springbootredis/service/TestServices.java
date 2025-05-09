package com.example.springbootredis.service;

import com.example.springbootredis.dto.OtherDTO;
import com.example.springbootredis.dto.TestDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TestServices {

    @Cacheable(value = "test",key = "#id")
    public TestDTO getTestDTO(Long id) {
        TestDTO testDTO = new TestDTO();
        testDTO.setId(1L);
        testDTO.setName("test");
        return testDTO;
    }

    @Cacheable(value = "test",key = "#id")
    public OtherDTO getOther(Long num) {
        OtherDTO testDTO = new OtherDTO();
        testDTO.setNum(1L);
        testDTO.setName("test");
        return testDTO;
    }
}
