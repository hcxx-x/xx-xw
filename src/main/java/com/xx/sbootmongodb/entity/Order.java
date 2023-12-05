package com.xx.sbootmongodb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author hanyangyang
 * @since 2023/12/5
 */
@Document
@Data
public class Order {
    /**
     * 如果以属性id作为主键，springdata 会自动将其映射到文档的_id字段，此时不要加@Field注解，否则可能会导致springdata无法正确的处理ObjectId
     * 如果不使用id作为主键时，可以通过@Field注解生命字段名称
     */
    @Id
    private String id;

    @Field("order_no")
    private String orderNo;

    @Field(value = "amount",targetType = FieldType.DECIMAL128)
    private BigDecimal amount;

    @Field("create_time")
    private LocalDateTime createTime;
}
