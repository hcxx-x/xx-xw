package com.example.project.gateway.utils;


import lombok.Data;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(asEnum = true)
@Data

public class JwtUserVO {

    private String tenantCode;


    private String id;


    private String institutionId;


    private String userType;


    private String name;


    private String mobile;


    private String secret;

}
