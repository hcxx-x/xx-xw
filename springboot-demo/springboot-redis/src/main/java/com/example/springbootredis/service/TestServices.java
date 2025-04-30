package com.example.springbootredis.service;

import com.example.springbootredis.dto.TestDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TestServices {

    @Cacheable(value = "test")
    public TestDTO getTestDTO() {
        TestDTO testDTO = new TestDTO();
        testDTO.setId(1L);
        testDTO.setName("test");
        return testDTO;
    }
}
