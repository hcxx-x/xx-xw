package com.xx.sbootmongodb;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.system.UserInfo;
import com.xx.sbootmongodb.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    /**
     * 查询操作
     *
     * 根据查询条件查询
     * public <T> List<T> find(Query query, Class<T> entityClass) {}
     * 根据查询条件查询返回一条记录
     * public <T> <T> findOne(Query query, Class<T> entityClass) {}
     * 查询该collection所有记录
     * public <T> List<T> findAll(Class<T> entityClass) {}
     */
    private void query() {
        // 组装查询条件（参数 Criteria 的详细用法见 criteriaUsageSample()）
        Query query = new Query(Criteria.where("username").is("admin"));
        // 查询唯一一条满足条件的数据（如果满足条件的数据多于1条，会报错）
        UserInfo one = mongoTemplate.findOne(query, UserInfo.class);
        // 查询满足条件的数据列表
        List<UserInfo> list = mongoTemplate.find(query, UserInfo.class);
        // 查询所有记录
        List<UserInfo> all = mongoTemplate.findAll(UserInfo.class);
        // 根据 filed 去重查询
        List<UserInfo> distinctList = mongoTemplate.findDistinct(query, "username", UserInfo.class, UserInfo.class);
        // 查询总数
        long count = mongoTemplate.count(query, UserInfo.class);
    }


    /**
     * 聚合操作
     */
    private void aggregate() {
        // 构造聚合操作列表
        List<AggregationOperation> operations = Lists.newArrayList(
                // 匹配操作（参数 Criteria 的详细用法见 criteriaUsageSample()）
                Aggregation.match(new Criteria()),
                // 随机操作（随机取 n 条数据）
                Aggregation.sample(10),
                // 分组操作（GroupOperation 的详细用法见 groupOperationUsageSample()）
                Aggregation.group(),
                // 关联操作
                Aggregation.lookup("targetCollectionName", "_id", "_id", "res"),
                // 拆分操作
                Aggregation.unwind("res"),
                // 排序操作
                Aggregation.sort(Sort.by("")),
                // 跳过操作
                Aggregation.skip(100),
                // 限制操作 （skip + limit 组合可以应用在复杂聚合的分页查询上）
                Aggregation.limit(10),
                // 投射操作（projectionOperation 的详细用法见 projectionOperationUsageSample()）
                Aggregation.project()
        );
        // 构造聚合函数
        Aggregation aggregation = Aggregation.newAggregation(operations);
        // 聚合查询
        AggregationResults<UserInfo> aggregate = mongoTemplate.aggregate(aggregation, UserInfo.class, UserInfo.class);
        // 获得聚合查询结果集
        List<UserInfo> mappedResults = aggregate.getMappedResults();
        // 获得聚合查询结果
        UserInfo uniqueMappedResult = aggregate.getUniqueMappedResult();
        // 如无具体类接收返回结果 可用 org.bson.Document 接收
        AggregationResults<Document> docs = mongoTemplate.aggregate(aggregation, UserInfo.class, Document.class);
        List<Document> results = docs.getMappedResults();
        for (Document result : results) {
            Optional.ofNullable(result.get("username")).orElse(null);
        }
    }

    /**
     * GroupOperation 分组操作样例
     */
    private void groupOperationUsageSample() {
        Aggregation.group("age")
                // 取分组后 age 字段里的第一个值 存入 age 字段
                .first("age").as("age")
                // 取分组后的数据总数 存入 count 字段
                .count().as("count")
                // 将分组后姓名存入 usernameList 列表
                .push("username").as("usernameList")
                // 将分组后姓名存入 usernameSet 集合， 去重
                .addToSet("username").as("usernameSet")
                // 取分组后学分总和
                .sum("score").as("scoreSum")
                // 取分组后学分平均数
                .avg("score").as("scoreAvg")
                // 取分组后学分最大值
                .max("score").as("scoreMax")
                // 取分组后学分最小值
                .min("score").as("scoreMin")
                // 取最后一条数据
                .last("$$ROOT").as("data");
    }
}
