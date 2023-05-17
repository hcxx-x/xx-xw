package com.gf.config.mp;

import cn.hutool.core.collection.CollUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Properties;

/**
 * 这个拦截器时是基于mybatis原生的拦截器，对执行的方法进行拦截
 * 在执行的时候会拦截mapper中的执行的方法，如果存在调用这直接调用自定义的BaseMapper中的insertOrUpdateOnDuplicateKey会被这里进行拦截
 * 然后执行一些必要的判断
 *
 * 如果是通过自定义的service调用方法，也会被这个拦截器拦截，虽然service中已经做了判断，但是因为service调用了mapper中的方法，所以会判断两次
 * 可以考虑将service中的判断拿掉
 *
 * @author hanyangyang
 * @since 2023/5/5
 */
@Component
@ConditionalOnProperty(prefix = "mybatis.updateOnDuplicateKey.autoKey", value = "check", havingValue = "true", matchIfMissing = true)
@Intercepts({ @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class DuplicateKeyUpdateAutoKeyCheckInterceptor implements Interceptor {
    /**
     * 日志记录器
     */
    private static final Logger logger= LoggerFactory.getLogger(DuplicateKeyUpdateAutoKeyCheckInterceptor.class);
    public static final String PARAMS_LIST = "list";

    public static final int LIST_SIZE_LIMIT=1;

    /**
     * intercept 方法用来对拦截的 sql 进行具体的操作
     * 本拦截方法只对 mapper 接口中的 searchByQuery 方法进行拦截，实际每个方法都拦截了，
     * 只是只有 searchByQuery 方法时，才真正执行 拦截的相关操作
     * @param invocation 拦截器执行器
     * @return
     * @throws Throwable 异常信息
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //  获取 invocation 传递的参数
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        // 获取执行的该拦截器的全路径方法 比如你的 UserInfoMapper 接口的 getById 方法， com.xx.UserInfoMapper.getById
        String id = ms.getId();
        String[] names = id.split("\\.");
        if (!names[names.length - 1].equals(CustomSqlInjector.METHOD_NAME_INSERT_OR_UPDATE_ON_DUPLICATE_KEY)){
            return invocation.proceed();
        }
        // 获取主键生成器，如果生成器是Jdbc3KeyGenerator则表示是自增主键
        KeyGenerator keyGenerator = ms.getKeyGenerator();
        if (keyGenerator instanceof Jdbc3KeyGenerator){
            // 该参数类型 org.apache.ibatis.binding.MapperMethod$ParamMap
            Object parameterObject = args[1];
            // 获取传递的参数
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameterObject;
            if (paramMap.containsKey(PARAMS_LIST)){
                Collection list = (Collection) paramMap.get(PARAMS_LIST);
                if (CollUtil.isNotEmpty(list) && list.size()>LIST_SIZE_LIMIT){
                    throw new RuntimeException("在主键自增环境下，"+CustomSqlInjector.METHOD_NAME_INSERT_OR_UPDATE_ON_DUPLICATE_KEY +
                            "方法的入参列表元素个数不能大于"+LIST_SIZE_LIMIT+
                            "否则会出现主键回填失败等异常，若不关心主键回填问题，可通过springboot配置文件中配置mybatis.updateOnDuplicateKey.autoKey.check:false关闭");
                }
            }
        }
        return invocation.proceed();
    }


    /***
     * 定义拦截的类 Executor、ParameterHandler、StatementHandler、ResultSetHandler当中的一个
     * @param target 需要拦截的类
     * @return
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;

    }

    /**
     * 属性相关操作
     * 设置和自定义属性值
     * @param properties 属性值
     */
    @Override
    public void setProperties(Properties properties) {
    }

}
