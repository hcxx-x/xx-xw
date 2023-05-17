package com.gf.config.mp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author hanyangyang
 * @since 2023/4/26
 */
public interface EnhanceBaseMapper<T> extends BaseMapper<T> {
    /**
     * 真正的批量插入,存在的问题：
     * 1、主键自增的时候可能不会回填
     * 2、只在mysql下测试过。
     * 3、会使数据库中设置了默认值的字段失效（关于这一点，也不难理解，批量插入的时候只能把所有的字段都列出来，因为每个实体可能插入的字段都不一样，也许实体1的字段1有值，字段2没有值，但是实体2可能是字段1没有值，字段2有值）
     *
     * 这些问题都可以在mybatis的源码中看到
     * 注意这个方法的名称要和定义sql注入器中 创建 InsertBatchSomeColumn 时的入参名称一样，如果没有入参，则使用默认的名称：insertBatchSomeColumn
     * @see CustomSqlInjector#getMethodList(Class, TableInfo)
     * @param list 要批量插入的列表
     * @return
     */
    boolean realBatchSave(List<T> list);

    /**
     * 根据主键更新,并且在更新时不忽略为null的字段
     * @param entity 实体数据
     * @return 更新成功 true 失败：false
     */
    boolean updateUnIgnoreById(T entity);


    /**
     * 插入或者更新数据，根据唯一索引判断，如果存在索引冲突则根据唯一索引更新，否则插入
     * @param entity 实体数据
     * @return 操作成功 true, 失败：false
     */
    boolean insertOrUpdate(T entity);

    /**
     * 插入数据并且在发生唯一索引冲突的时候会更新数据
     *
     * 该方法虽然被Deprecated标注，但是只是为了提示调用这个方法用风险，不太建议使用，如果想要使用注意看一下下面的注意事项，并做好测试
     *
     * 注意事项：
     *  1、实现方式是使用on duplicate key update 方式，所以目前只能在mysql环境中使用
     *  2、如果是自增主键，则这个方法返回的自增主键会有问题，不建议再使用自增主键的时候使用这个这个方法，但是有一种方式例外——每次list中只包含一个元素的时候，只有在这种场景可以自增主键可能有用，但是也要注意测试
     *  3、调用这个方法的时候如果只发生更新，自增主键也会增加，如果要修改这个问题，则需要修改mysql的参数 innodb_autoinc_lock_mode （不建议），修改为0，这样就可以再只有真正插入的时候才会让自增主键 自增
     *      关于innodb_autoinc_lock_mode 的文档可以查看官方文档：https://dev.mysql.com/doc/refman/8.0/en/innodb-auto-increment-handling.html#innodb-auto-increment-initialization
     *      以及其他人翻译的文档：https://blog.csdn.net/ashic/article/details/53810319
     *  4、如果一个表存在多个唯一索引，并且在执行on duplicate key update的时候匹配到了多个冲突数据，则只会更新第一条数据
     *      如： 表 A 有字段 B,C,D,并且字段B和D都建立了唯一索引，切现在有数据（'a','b','c'） ('d','e','f')
     *           现在要使用on duplicate key update插入或者更新一条数据 （'a','g','f'）,可以发现，这个数据和上面两个数据都存在唯一索引的冲突
     *           此时之后使用 on duplicate key update 只会更新一条，可能是第一条，也可能是第二条，没有测试过，使用时需要特别注意
     *
     *  参考：
     *  1、https://blog.csdn.net/qq_27680317/article/details/81118070
     *  2、https://blog.csdn.net/slvher/article/details/42298355https://blog.csdn.net/slvher/article/details/42298355
     *
     * @param entities 要插入或者更新的实体数据
     * @return
     */
    @Deprecated
    boolean insertOrUpdateOnDuplicateKey(@Param("list") Collection<T> entities);
}
