package org.example.velocity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author hanyangyang
 * @date 2025/5/15
 */
@Data
@ToString(callSuper = true)
public class RuleResult {

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段属性
     */
    private String field;

    /**
     * 条件： eq: 等于   pattern：表达式
     */
    private String condition;

    /**
     * 结果值：可以是返回值，也可以是表达式ID
     */
    //private String resultValue;

    private List<String> resultValue;
}



