package org.example.designmode.abstractfactory.product.impl;

import org.example.designmode.abstractfactory.product.ICar;

/**
 * @author hanyangyang
 * @since 2025/8/16
 **/
public class HaierCar implements ICar {
    @Override
    public void run() {
        System.out.println("海尔造车了");
    }
}
