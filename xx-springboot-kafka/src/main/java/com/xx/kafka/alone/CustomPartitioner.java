package com.xx.kafka.alone;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.Random;

/**
 * @author hanyangyang
 * @since 2023/5/18
 */
public class CustomPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // 获取topic的分区数量
        Integer partitionCount = cluster.partitionCountForTopic(topic);
        // 随机返回一个大于0小于分区数量的分区编号
        return new Random().nextInt(partitionCount);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
