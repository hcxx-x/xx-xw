package com.xx.sbootmongodb;

import cn.hutool.core.util.RandomUtil;
import com.xx.sbootmongodb.entity.Order;
import com.xx.sbootmongodb.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author hanyangyang
 * @since 2023/12/5
 */
@SpringBootTest
public class OrderRepositoryTest {

    @Resource
    private OrderRepository orderRepository;

    @Test
    public void testSave(){
        Order order = Order.builder()
                .orderNo(UUID.randomUUID().toString())
                .amount(RandomUtil.randomBigDecimal())
                .createTime(LocalDateTime.now()).build();
        orderRepository.save(order);
    }

    @Test
    public void testUpdateSave(){
        Order order = Order.builder()
                .orderNo("111")
                .amount(new BigDecimal(12))
                .id("656e92d7699f2204f410a575")
                .build();
        // 保存时如果_id对应的值已经存在，则更新,否则插入
        orderRepository.save(order);

    }
}
