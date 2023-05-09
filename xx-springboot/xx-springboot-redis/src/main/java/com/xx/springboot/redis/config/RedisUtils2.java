package com.xx.springboot.redis.config;

/**
 * @author hanyangyang
 * @since 2023/5/9
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 小胖
 * @Version: 1.0
 * @Description: redis工具
 * @Date: 2020/11/09  11:36
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public  class RedisUtils2 {
    @Autowired
    public static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisUtils2.redisTemplate = redisTemplate;
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间  时间(秒)
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout, final TimeUnit unit)
    {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static long getExpire(String key)
    {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key)
    {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public static boolean del(final String key)
    {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public static long del(final Collection collection)
    {
        return redisTemplate.delete(collection);
    }


    //============================String=============================


    /**
     * 普通缓存获取。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public static <T> T get(final String key)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public static Collection<String> keys(final String pattern)
    {
        return redisTemplate.keys(pattern);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public static <T> void set(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @param time  时间 (秒)
     */
    public static <T> void set(final String key, final T value, final Integer time)
    {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }


    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param time     时间
     * @param timeUnit 时间颗粒度
     */
    public static <T> void set(final String key, final T value, final Integer time, final TimeUnit timeUnit)
    {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static long incr(final String key, final long delta)
    {
        if (delta < 0)
        {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static long decr(final String key, final long delta)
    {
        if (delta < 0)
        {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    //================================Map=================================

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static <T> T hget(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public static <T> List<T> hgets(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static <T> Map<String, T> hmget(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public static <T> void hmset(final String key, final Map<String, T> map)
    {
        if (map != null)
        {
            redisTemplate.opsForHash().putAll(key, map);
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     */
    public static <T> void hmset(final String key, final Map<String, T> map, final long time)
    {
        if (map != null)
        {
            redisTemplate.opsForHash().putAll(key, map);
            expire(key, time);
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间
     * @param unit 时间单位
     */
    public static <T> void hmset(final String key, final Map<String, T> map, final long time, final TimeUnit unit)
    {
        if (map != null)
        {
            redisTemplate.opsForHash().putAll(key, map);
            expire(key, time, unit);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param hKey  项
     * @param value 值
     */
    public static <T> void hset(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param hKey  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     */
    public static <T> void hset(final String key, final String hKey, final T value, final long time)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
        expire(key, time);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param hKey  项
     * @param value 值
     * @param time  时间  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @param unit  时间单位
     */
    public static <T> void hset(final String key, final String hKey, final T value, final long time, final TimeUnit unit)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
        expire(key, time, unit);
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param hKey 项 可以使多个 不能为null
     */
    public static void hdel(final String key, final Object... hKey)
    {
        redisTemplate.opsForHash().delete(key, hKey);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param hKey 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(final String key, final String hKey)
    {
        return redisTemplate.opsForHash().hasKey(key, hKey);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param hKey 项
     * @param by   要增加几(大于0)
     * @return
     */
    public static double hincr(final String key, final String hKey, final double by)
    {
        return redisTemplate.opsForHash().increment(key, hKey, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param hKey 项
     * @param by   要减少记(小于0)
     * @return
     */
    public static double hdecr(final String key, final String hKey, final double by)
    {
        return redisTemplate.opsForHash().increment(key, hKey, -by);
    }

    //================================Set=================================

    /**
     * 将数据放入set缓存
     *
     * @param key 缓存键值
     * @param set 缓存的数据
     * @return 缓存数据的数量
     */
    public static <T> long sSet(final String key, final Set<T> set)
    {
        Long count = redisTemplate.opsForSet().add(key, set);
        return count == null ? 0 : count;
    }

    /**
     * 将数据放入set缓存   并设置时间
     *
     * @param key  缓存键值
     * @param set  缓存的数据
     * @param time 时间(秒)
     * @return 缓存数据的数量
     */
    public static <T> long sSet(final String key, final Set<T> set, final long time)
    {
        Long count = redisTemplate.opsForSet().add(key, set);
        expire(key, time);
        return count == null ? 0 : count;
    }

    /**
     * 将数据放入set缓存   并设置时间
     *
     * @param key  缓存键值
     * @param set  缓存的数据
     * @param time 时间
     * @param unit 时间单位
     * @return 缓存数据的数量
     */
    public static <T> long sSet(final String key, final Set<T> set, final long time, final TimeUnit unit)
    {
        Long count = redisTemplate.opsForSet().add(key, set);
        expire(key, time, unit);
        return count == null ? 0 : count;
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(final String key, final Object... values)
    {
        Long count = redisTemplate.opsForSet().add(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(final String key, final long time, final Object... values)
    {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time);
        return count == null ? 0 : count;
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param unit   时间单位
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(final String key, final long time, final TimeUnit unit, final Object... values)
    {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time, unit);
        return count == null ? 0 : count;
    }


    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public static boolean sHasKey(final String key, final Object value)
    {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 根据key获取Set中的所有值
     *
     * @param key
     * @return
     */
    public static <T> Set<T> sGet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long sGetSetSize(final String key)
    {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static long setRemove(String key, Object... values)
    {
        Long count = redisTemplate.opsForSet().remove(key, values);
        return count == null ? 0 : count;
    }

    //================================List=================================

    /**
     * 获得所有缓存的list缓存的内容
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public static List lGet(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 获取指定位置list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public static <T> List<T> lGet(final String key, final long start, final long end)
    {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public static <T> T lGetIndex(final String key, final long index)
    {
        ListOperations<String, T> opsForList = redisTemplate.opsForList();
        return opsForList.index(key, index);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long lGetListSize(final String key)
    {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 缓存List数据
     *
     * @param key  缓存的键值
     * @param list 待缓存的List数据
     * @return 缓存的对象
     */
    public static <T> long lSet(final String key, final List<T> list)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, list);
        return count == null ? 0 : count;
    }

    /**
     * 缓存List数据
     *
     * @param key  缓存的键值
     * @param list 待缓存的List数据
     * @param time 时间(秒)
     * @return 缓存的对象
     */
    public static <T> long lSet(final String key, final List<T> list, final long time)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, list);
        expire(key, time);
        return count == null ? 0 : count;
    }

    /**
     * 缓存List数据
     *
     * @param key  缓存的键值
     * @param list 待缓存的List数据
     * @param time 时间
     * @param unit 时间单位
     * @return 缓存的对象
     */
    public static <T> long lSet(final String key, final List<T> list, final long time, final TimeUnit unit)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, list);
        expire(key, time, unit);
        return count == null ? 0 : count;
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> long lSet(final String key, final T value)
    {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static <T> long lSet(final String key, final T value, final long time)
    {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return count == null ? 0 : count;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间
     * @param unit  时间单位
     * @return
     */
    public static <T> long lSet(final String key, final T value, final long time, final TimeUnit unit)
    {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time, unit);
        return count == null ? 0 : count;
    }


    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public static <T> void lUpdateIndex(final String key, final long index, final T value)
    {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static long lRemove(final String key, final long count, final Object value)
    {
        Long remove = redisTemplate.opsForList().remove(key, count, value);
        return remove == null ? 0 : remove;
    }

    /**
     * 弹出最左边的元素，弹出之后该值在列表中将不复存在
     * @param key   键
     * @return  弹出的值
     */
    public static <T> T leftPop(final String key)
    {
        ListOperations<String, T> opsForList = redisTemplate.opsForList();
        return opsForList.leftPop(key);
    }
}


