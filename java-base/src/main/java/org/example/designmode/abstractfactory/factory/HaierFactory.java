package org.example.designmode.abstractfactory.factory;

import org.example.designmode.abstractfactory.product.ICar;
import org.example.designmode.abstractfactory.product.ITV;
import org.example.designmode.abstractfactory.product.impl.HaierCar;
import org.example.designmode.abstractfactory.product.impl.HaierTV;

/**
 * @author hanyangyang
 * @since 2025/8/16
 **/
public class HaierFactory implements AbstractFactory {

    @Override
    public ITV createTV() {
        return new HaierTV();
    }

    @Override
    public ICar createCar() {
        return new HaierCar();
    }
}
