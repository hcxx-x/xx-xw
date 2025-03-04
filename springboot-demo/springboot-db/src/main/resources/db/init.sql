CREATE TABLE `user`
(
    `id`    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`  VARCHAR(255)     NULL COMMENT '用户名',
    `phone` VARCHAR(20)      NULL COMMENT '手机号',
    `gender` VARCHAR(20)      NULL COMMENT '性别',
    `gmt_create` datetime     NULL default current_timestamp() COMMENT '创建时间',
    `gmt_modified` datetime     NULL default current_timestamp() on update current_timestamp() COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';
