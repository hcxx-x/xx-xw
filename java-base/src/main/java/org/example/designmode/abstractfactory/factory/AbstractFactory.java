package org.example.designmode.abstractfactory.factory;

import org.example.designmode.abstractfactory.product.ICar;
import org.example.designmode.abstractfactory.product.ITV;

/**
 * 抽象工厂
 * @author hanyangyang
 * @since 2025/8/16
 **/
public interface AbstractFactory {
    ITV createTV();

    ICar createCar();
}
