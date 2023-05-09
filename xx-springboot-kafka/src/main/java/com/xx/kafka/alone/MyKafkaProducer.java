package com.xx.kafka.alone;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author hanyangyang
 * @since 2023/5/9
 */
public class MyKafkaProducer {
    public static void main(String[] args) {
        // 1. 创建 kafka 生产者的配置对象
        Properties properties = new Properties();
        // 2. 给 kafka 配置对象添加配置信息：bootstrap.servers
        // 这里可以通过域名和ip进行链接，如果kafka服务运行在虚拟机中，并且在配置过程中使用了自定义域名（host 映射）那么这里就需要在代码运行的机器上也要添加host映射
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka0:9092");

        // key,value 序列化（必须）：key.serializer，value.serializer
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        // 3. 创建 kafka 生产者对象
        KafkaProducer<String, String> kafkaProducer = new
                KafkaProducer<String, String>(properties);
        // 4. 调用 send 方法,发送消息
        for (int i = 0; i < 5; i++) {
            kafkaProducer.send(new
                    ProducerRecord<>("first","msg " + i));
        }
        // 5. 关闭资源
        kafkaProducer.close();
    }

}

