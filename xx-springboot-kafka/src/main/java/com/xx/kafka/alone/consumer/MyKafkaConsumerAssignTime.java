package com.xx.kafka.alone.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

/**
 * @author hanyangyang
 * @since 2023/5/26
 */
public class MyKafkaConsumerAssignTime {
    public static void main(String[] args) {
        // 1.创建消费者的配置对象
        Properties properties = new Properties();
        // 2.给消费者配置对象添加参数
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka0:9092");
        // 配置序列化 必须
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        // 配置消费者组（组名任意起名） 必须
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        // 修改分区分配策略
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RoundRobinAssignor");
        // 创建消费者对象
        KafkaConsumer<String, String> kafkaConsumer = new
                KafkaConsumer<String, String>(properties);
        // 注册要消费的主题（可以消费多个主题）
        ArrayList<String> topics = new ArrayList<>();
        topics.add("first");
        kafkaConsumer.subscribe(topics);


        Set<TopicPartition> assignment = new HashSet<>();
        while (assignment.size() == 0) {
            // 这里要先拉取一次数据，因为分区的分配是在 poll() 方法的调用过程中实现的，也就是说，在执行 seek() 方法之前需要先执行一次 poll() 方法，等到分配到分区之后才可以重置消费位置。
            // 在指定分区消费之前的数据我们肯定是不需要的，所以这里不用接受消息，即也不会处理这一批消息，只有在真正指定过分区之后才会处理分区的消息
            kafkaConsumer.poll(Duration.ofSeconds(1));
            // 获取消费者分区分配信息（有了分区分配信息才能开始消费）
            assignment = kafkaConsumer.assignment();
        }

        HashMap<TopicPartition, Long> timestampToSearch = new HashMap<>();
        // 封装集合存储，指定每个分区要从什么时间开始消费，比如下面就是从一天前开始消费
        for (TopicPartition topicPartition : assignment) {
            timestampToSearch.put(topicPartition,
                    System.currentTimeMillis() - 24 * 3600 * 1000);
        }
        // 获取指定时间前的每个分区的 offset
        Map<TopicPartition, OffsetAndTimestamp> offsets =
                kafkaConsumer.offsetsForTimes(timestampToSearch);
        // 遍历每个分区，获取每个分区对应时间的offset。
        for (TopicPartition topicPartition : assignment) {
            OffsetAndTimestamp offsetAndTimestamp =
                    offsets.get(topicPartition);
            // 指定offset
            if (offsetAndTimestamp != null) {
                kafkaConsumer.seek(topicPartition,
                        offsetAndTimestamp.offset());
            }
        }


        // 拉取数据打印
        while (true) {
            // 设置 1s 消费一批数据
            ConsumerRecords<String, String> consumerRecords =
                    kafkaConsumer.poll(Duration.ofSeconds(1));
            // 打印消费到的数据
            for (ConsumerRecord<String, String> consumerRecord :
                    consumerRecords) {
                System.out.println(consumerRecord);
            }
        }
    }


}
