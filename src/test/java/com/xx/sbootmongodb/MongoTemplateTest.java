package com.xx.sbootmongodb;

import cn.hutool.core.util.RandomUtil;
import com.xx.sbootmongodb.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author hanyangyang
 * @since 2023/12/5
 */
@Slf4j
@SpringBootTest
public class MongoTemplateTest {

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void test(){
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setAmount(RandomUtil.randomBigDecimal());
        order.setCreateTime(LocalDateTime.now());
        mongoTemplate.save(order);
    }

    @Test
    public void testQuery(){
        ObjectId id = new ObjectId("656e92d7699f2204f410a575");
        List<Order> orderList = mongoTemplate.find(new Query().addCriteria(Criteria.where("_id").is(id.toString())), Order.class,"order");
        log.info("orderList:{}",orderList);
    }
}
