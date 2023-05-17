package com.xx.kafka.alone;

import org.apache.kafka.clients.producer.*;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 同步发送到底同步在哪里？
 * 如果是异步发送的话是将消息追加到队列中就不管了，等待达到batch size或者linger.ms之后，sender线程启动发送消息
 * 而如果是同步发送的话，则是调用了一次get方法，而这个get方法最终调用的是FutureRecordMetadata的get方法
 * 在这个get方法中又会调用ProduceRequestResult的await方法，而ProduceRequestResult中存在一个计数器(CountDownLatch)为1的计数器
 * 而await方法最终会调用这个计数器的await方法，此时main线程就会阻塞
 * 当达到batch size或者linger.ms之后，sender线程启动发送消息，无论消息发送成功或者失败，都会将技术器清零，之后main线程阻塞解除
 *
 *
 * 所以，同步发送真正同步的地方在于同步发送将会在调用get方法之后等待sender线程真正将消息发出或者说是sender发送一次数据后再继续运行主线程
 *
 * 大致流程：
 * producer 调用 send 方法
 * send方法中将消息放到 累加器RecordAccumulator中，累加器里面维护了一个ConcurrentMap 这个map的Key为分区，value为 producerBatch
 * 相当于为每一个分区维护了一个producerBatch, 而producerBatch中又有一个ProduceRequestResult对象，这个对象中就维护了一个计数器，
 * 同步发送最终就会调用到这个对象中技术器的await，导致线程阻塞。而sender线程会从累加器中取出producerBatch，并最终会调用ProduceRequestResult对象里面那个计数器的countDown方法
 * 导致计数器清零，从而解除main线程的阻塞
 *
 *
 *
 * @author hanyangyang
 * @since 2023/5/9
 */
public class MyKafkaSyncProducerWithCallback {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
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

        properties.put(ProducerConfig.BATCH_SIZE_CONFIG,16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG,10000);

        // 3. 创建 kafka 生产者对象
        KafkaProducer<String, String> kafkaProducer = new
                KafkaProducer<String, String>(properties);
        // 4. 调用 send 方法,发送消息
        for (int i = 0; i < 5; i++) {
            String msg = "msg with callback 序号： " + i;
            kafkaProducer.send(new ProducerRecord<>("first", msg), new Callback() {
                /**
                 * 这个方法会在生产者不到ack后异步调用
                 * @param recordMetadata 消息的一些元素数据信息，比如topic,partition,offset
                 * @param e 如果发送失败则会给出对应的异常，发送成功则为空
                 */
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (Objects.isNull(e)){
                        System.out.println(String.format("消息发送成功，topic:%s, partition:%s",recordMetadata.topic(),recordMetadata.partition()));
                    }else{
                        System.out.println("消息发送失败：消息内容"+recordMetadata);
                        e.printStackTrace();
                    }
                }
            });
        }
        TimeUnit.SECONDS.sleep(15);
        // 5. 关闭资源
        kafkaProducer.close();
    }

}

