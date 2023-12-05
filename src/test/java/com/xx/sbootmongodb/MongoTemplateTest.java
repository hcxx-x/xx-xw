package com.xx.sbootmongodb;

import cn.hutool.core.util.RandomUtil;
import com.xx.sbootmongodb.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.bson.Document;
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


    /**
     * Criteria 使用样例
     */
    private void criteriaUsageSample() {
        // 精确查询 { "username" : "admin" } 初始化 criteria 实例的两种方法
        Criteria criteria = Criteria.where("username").is("admin");
        Criteria cri = new Criteria("username").is("admin");

        // 不等于查询 { "username" : { $ne : "admin" } }
        Criteria ne = Criteria.where("username").ne("admin");

        // 模糊查询 { "username" : /admin/ }
        Criteria regex = Criteria.where("username").regex("^.*" + "admin" + ".*$");

        // and 查询 { "username" : "admin", "phoneNumber" : "10086" }
        Criteria and = criteria.and("phoneNumber").is("10086");
        and = new Criteria().andOperator(Criteria.where("username").is("admin"),
                Criteria.where("phoneNumber").is("10086"));

        // or 查询 $or:[ { "username" : "admin" }, { "username" : "anonymous" } ]
        Criteria or = criteria.orOperator(Criteria.where("username").is("admin"),
                Criteria.where("username").is("anonymous"));

        // in 查询 { "username" : { $in: ["admin", "anonymous"] } }
        Criteria in = Criteria.where("username").in(Lists.newArrayList("admin", "anonymous"));

        // nin 查询 { "username" : { $nin: ["admin", "anonymous"] } }
        Criteria nin = Criteria.where("username").nin(Lists.newArrayList("admin", "anonymous"));

        // lt/lte 比较查询
        // 小于等于 { "crtDateTime": { $lte: ISODate("2001-01-01T00:00:00.000+08:00") } }
        Criteria lte = Criteria.where("crtDateTime").lte(LocalDateTime.now());
        // { "age": { $lte: 18 } }
        lte = Criteria.where("age").lte(18);

        // 小于 { "crtDateTime": {$lt: ISODate("2001-01-01T00:00:00.000+08:00") } }
        Criteria lt = Criteria.where("crtDateTime").lt(LocalDateTime.now());
        // { "age": { $lt: 18 } }
        lt = Criteria.where("age").lt(18);

        // gt/gte 比较查询
        // 大于等于 { "crtDateTime": {$gte: ISODate("2001-01-01T00:00:00.000+08:00") } }
        Criteria gte = Criteria.where("crtDateTime").gte(LocalDateTime.now());
        // { "age": { $gte: 18 } }
        gte = Criteria.where("age").gte(18);

        // 大于 { "crtDateTime": {$gt: ISODate("2001-01-01T00:00:00.000+08:00") } }
        Criteria gt = Criteria.where("crtDateTime").gt(LocalDateTime.now());
        // { "age": { $gt: 18 } }
        gt = Criteria.where("age").gt(18);

        // 查询内嵌文档 { "usernameList" : { $elemMatch: { "username" : "admin" } } }
        Criteria elemMatch = Criteria.where("usernameList").elemMatch(Criteria.where("username").is("admin"));

        // api 中无具体接口的， 使用 document 拼接语句查询 { $expr : { $ne : [ "$A", "$B" ] } }
        Criteria andDocumentStructureMatches = criteria.andDocumentStructureMatches(() ->
                new Document().append("$expr", new Document("$ne", List.of("$A", "$B"))));
    }
}
