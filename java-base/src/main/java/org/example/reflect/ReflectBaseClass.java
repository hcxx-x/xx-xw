package org.example.reflect;

import lombok.ToString;

/**
 * @author hanyangyang
 * @since 2025/8/16
 **/
@ToString
public class ReflectBaseClass {
    private String privateField;

    public ReflectBaseClass(String privateField) {
        this.privateField = privateField;
    }

    public void setPrivateField(String field){
        this.privateField = field;
    }
}
