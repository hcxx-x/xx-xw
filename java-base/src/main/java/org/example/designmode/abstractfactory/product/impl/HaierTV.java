package org.example.designmode.abstractfactory.product.impl;

import org.example.designmode.abstractfactory.product.ITV;

/**
 * @author hanyangyang
 * @since 2025/8/16
 **/
public class HaierTV implements ITV {
    private String brand;


    @Override
    public void gongneng() {
        System.out.println("可以洗衣服");
    }
}
