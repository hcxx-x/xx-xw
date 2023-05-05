package com.xx.gf;

import com.gf.config.mp.CodeGeneratorUtil;

/**
 * @author hanyangyang
 * @since 2023/5/5
 */
public class CodeGenerator {
    public static void main(String[] args) {
        CodeGeneratorUtil.generator("localhost:3306","root","root","hyy_test");
    }
}
