package com.xx.sbootmongodb.repository;

import com.xx.sbootmongodb.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hanyangyang
 * @since 2023/12/5
 */
public interface OrderRepository extends MongoRepository<Order,String> {
}
