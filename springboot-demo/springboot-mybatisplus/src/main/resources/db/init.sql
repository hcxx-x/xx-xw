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


-- 查询中使用变量的示例 (变量初始化再from后面，相当于 users 表笛卡尔积后面只有一条且只有一列的表，然后再对每一行的@i:=@i+1计算赋值)
-- SELECT @i:=@i + 1 AS row_id, name FROM users, (SELECT @i:=100) AS init;

-- 创建示例表
CREATE TABLE person(
                       id int NOT NULL AUTO_INCREMENT PRIMARY KEY comment '主键',
                       person_id tinyint not null comment '用户id',
                       person_name VARCHAR(200) comment '用户名称',
                       gmt_create datetime comment '创建时间',
                       gmt_modified datetime comment '修改时间'
) comment '人员信息表';

-- 往示例表中循环插入数据
-- 设置变量i值为1
set @i=1;
-- insert ... select 插入数据
insert into person(id, person_id, person_name, gmt_create, gmt_modified)
-- 查询数据并对变量进行累加，每查出一条数据变量i+1
select @i:=@i+1,
       left(rand()*10,10) as person_id,
       concat('user_',@i%2048),
       date_add(gmt_create,interval + @i*cast(rand()*100 as signed) SECOND),
       date_add(date_add(gmt_modified,interval +@i*cast(rand()*100 as signed) SECOND), interval + cast(rand()*1000000 as signed) SECOND)
from person;
