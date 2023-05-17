package com.gf.config.mp;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author hanyangyang
 * @since 2023/4/26
 */
public interface IEnhanceService<T> extends IService<T> {

    /**
     * 真正的批量插入,只实现批量插入，不实现批量更新
     * 注意这个方法的名称要和定义sql注入器中 创建 InsertBatchSomeColumn 时的入参名称一样，如果没有入参，则使用默认的名称：insertBatchSomeColumn
     * @see CustomSqlInjector#getMethodList(Class, TableInfo)
     * @param entitys 要批量插入的列表
     * @return
     */
    void realBatchSave(Collection<T> entitys);


    /**
     * 插入数据并切在发生唯一索引冲突的时候会更新数据
     *
     * 该方法虽然被Deprecated标注，但是只是为了提示调用这个方法用风险，不太建议使用，如果想要使用注意看一下下面的注意事项，并做好测试
     *
     * 注意事项：
     *  1、实现方式是使用on duplicate key update 方式，所以目前只能在mysql环境中使用
     *  2、如果是自增主键，则每次调用这个方法都只会返回一个主键，这也是本方法只提供了对象入参，没有提供列表入参的原因，如果使用的不是自增主键，则可以尝试实现列表入参，但是注意测试
     *  3、调用这个方法的时候如果只发生更新，自增主键也会增加，如果要修改这个问题，则需要修改mysql的参数 innodb_autoinc_lock_mode （不建议），修改为0，这样就可以再只有真正插入的时候才会让自增主键 自增
     *      关于innodb_autoinc_lock_mode 的文档可以查看官方文档：https://dev.mysql.com/doc/refman/8.0/en/innodb-auto-increment-handling.html#innodb-auto-increment-initialization
     *      以及其他人翻译的文档：https://blog.csdn.net/ashic/article/details/53810319
     *  4、如果一个表存在多个唯一索引，并且在执行on duplicate key update的时候匹配到了多个冲突数据，则只会更新第一条数据
     *    如： 表 A 有字段 B,C,D,并且字段B和D都建立了唯一索引，切现在有数据（'a','b','c'） ('d','e','f')
     *         现在要使用on duplicate key update插入或者更新一条数据 （'a','g','f'）,可以发现，这个数据和上面两个数据都存在唯一索引的冲突
     *         此时之后使用 on duplicate key update 只会更新一条，可能是第一条，也可能是第二条，没有测试过，使用时需要特别注意
     *
     *  参考：
     *  1、https://blog.csdn.net/qq_27680317/article/details/81118070
     *  2、https://blog.csdn.net/slvher/article/details/42298355https://blog.csdn.net/slvher/article/details/42298355
     *
     * @param t 要插入或者更新的实体数据
     * @return
     */
    void insertOrUpdateOnDuplicateKey(T t);


    /**
     * 注意参考上本方法的重载方法
     * @see IEnhanceService#insertOrUpdateOnDuplicateKey(Object)
     * @param collection 入参集合
     * @return
     */
    void insertOrUpdateOnDuplicateKey(Collection collection);
}
