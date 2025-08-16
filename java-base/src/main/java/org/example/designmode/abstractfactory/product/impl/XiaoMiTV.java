package org.example.designmode.abstractfactory.product.impl;

import org.example.designmode.abstractfactory.product.ITV;

/**
 * @author hanyangyang
 * @since 2025/8/16
 **/
public class XiaoMiTV implements ITV
{
    @Override
    public void gongneng() {
        System.out.println("不仅可以洗衣服还可以洗鞋子");
    }
}
