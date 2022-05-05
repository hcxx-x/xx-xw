下面的内容引用了CSDN的一篇文档，地址：https://blog.csdn.net/lmy86263/article/details/71304629

在使用Hibernate的时候我们一般都会配置hibernate.hbm2ddl.auto这个属性，而其中的常见的属性值包括：
```
create
create-drop
validate
update
```
而在使用的过程中，update是我用的比较多的，由于官方文档对于这个属性产生的效果和使用的范围没有做详细的说明，只是提了一句会会更新数据库的schema，而且不建议在生产环境中使用，但是怎么更新，更新都包括哪些内容？完全没有提到，因此为了弄清其中的实现，从以下几个方面验证：

# 一、实体变化
## 1、实体名称变化
实体名称变化，原来实体生成的表不删除，而是生成新的实体的表。

## 2、实体索引变化
索引的设置使用的是@Table中的indexes的属性，这个属性指的是@Index注解，其中常用的属性有：

columnList：指的是类中字段的名称；
unique：标识该索引是不是唯一；

当之前没有添加索引的时候，如果使用@Index添加索引时则会生成新的索引；如果之前设置了索引，之后在实体类中删除该索引，但是在数据库层次上并不会删除该索引；

# 二、字段变化
## 1、字段添加/删除
类中添加新的字段，则生成新的数据表列，而不删除或者影响其他的列；

类中删除一个字段，则该字段在数据库中生成的列不会删除，对应的数据库属性也不会受到影响；

## 2、字段改变
### 2.1名称改变
类中一个字段的名称变化，这相当于添加一个字段和删除一个字段，
- 原来的字段：相当于被删除了，但是该字段生成的列不会在数据库中删除，对应的属性也不会收到影响；

新加的字段：生成新的数据库列，而不影响现有的数据库列；
### 2.2字段的属性改变
#### 2.2.1 主键变化
在Hibernate中，使用JPA中的@javax.persistence.Id注解标识一个@Entity类的属性，这个@Id属性有以下几个特点：

在一个@Entity类必须有@Id标识的字段，可以多于一个，如果有多个则是生成复合主键，不是多个主键，例如在MySQL中生成符合主键时，实体类如下：
```java
@Entity
public class User {
    @Id
    private Long id;
    @Id
    private long id0;
    @Column
    private String name;
}
```


生成的语句如下：
```sql
CREATE TABLE `user` (
    `id0` bigint(20) NOT NULL,
    `id` bigint(20) NOT NULL,
    `name` varchar(255) DEFAULT NULL
    PRIMARY KEY (`id0`,`id`)
)
```

该@Id注解只能被放在Java的基本类型以及其包装类型，比如int和Integer等；

如果在主键变化后，会产生不同的效果，在以下两种情况中：

之前有@Id注解的字段，后来没有@Id注解的字段：这中情况下会报出异常org.hibernate.AnnotationException: No identifier specified for entity；
但是不存在之前无@Id注解的字段，而之后可以存在的这种情况；
#### 2.2.2 列属性变化
在Hibernate中如果想设置数据表列的属性时，使用的是@Column注解，在这个注解中的的常用的属性包括：

unique、nullable、length
如果在之前没有设置这个unique、nullable等的属性，即使后来设置了，在数据库表中列的属性也不会改变；
反过来说，之前如果设计了，如果再删除，这些属性也不会取消。
换句话说，一旦你针对同样的字段的列已经生成，即使你后来再改变属性（不包括索引），只要列名不变，属性就不会变。
