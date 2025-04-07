package com.example.project.gateway.utils;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类,RedisTemplate 实现
 *
 * @author Cao Guangwen
 */
@Component
@Slf4j
public class RedisUtils {

    public static RedisTemplate<String, Object> redisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("expire() is error : {}", key, e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * key的值加一，如果可以不存在也會返回1
     * @param key
     * @return
     */
    public static long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * key的值加delta，如果可以不存在也會返回delta
     * @param key key
     * @param delta 字增量
     * @return 当前值
     */
    public static long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("hasKey() is error : {}", e);
            return false;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, JSONArray.toJSONString(value));
            return true;
        } catch (Exception e) {
            log.error("set() is error : {}", e);
            return false;
        }
    }

    public static Set<String> keys(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            return keys;
        } catch(Exception e) {
            log.error("keys() is error : {}", e);
            return new HashSet<>();
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("set() is error : {}", key, e);
            return false;
        }
    }


    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean setValueToString(String key, Object value, long time) {
        try {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(value, JSONWriter.Feature.WriteClassName), time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean setValueToString(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(value, JSONWriter.Feature.WriteClassName));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public static Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public static boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("hmset() is error : {}", e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public static boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("hmset() is error : {}", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("hset() is error : {}", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("hset() is error : {}", e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public static void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public static double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public static double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public static Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("sGet() is error : {}", e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public static boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("sHasKey() is error : {}", e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("sSet() is error : {}", e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("sSetAndTime() is error : {}", e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("sGetSetSize() is error : {}", e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("setRemove() is error : {}", e);
            return 0;
        }
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public static List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("lGet() is error : {}", e);
            return null;
        }
    }

    /**
     * 获取list缓存的内容
     *
     * @param key 键
     * @return
     */
    public static List<Object> lGet(String key) {
        return lGet(key, 0, -1);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("lGetListSize() is error : {}", e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public static Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("lGetIndex() is error : {}", e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("lSet() is error : {}", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("lSet() is error : {}", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("lSet() is error : {}", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("lSet() is error : {}", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public static boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("lUpdateIndex() is error : {}", e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("lRemove() is error : {}", e);
            return 0;
        }
    }

    /**
     * 获取对象
     *
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T getObjectBean(String key, Class<T> clazz) {

        String value = (String) redisTemplate.opsForValue().get(key);
        return parseJson(value, clazz);
    }

    /**
     * 存放对象
     *
     * @param key
     * @param obj
     */
    public static <T> void setObjectBean(String key, T obj) {
        if (obj == null) {
            return;
        }

        String value = toJson(obj);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 存放对象并返回该对象
     *
     * @param key
     * @param obj
     */
    public static <T> T getAndSet(String key, T obj, Class<T> clazz) {
        if (obj == null) {
            return getObjectBean(key, clazz);
        }
        String value = (String) redisTemplate.opsForValue().getAndSet(key, toJson(obj));
        return parseJson(value, clazz);
    }

    /**
     * 原子自增
     *
     * @param key
     * @return
     */
    public static long generate(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.incrementAndGet();
    }

    /**
     * A原子自增
     *
     * @param key
     * @param expireTime
     * @return
     */
    public static long generate(String key, Date expireTime) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        counter.expireAt(expireTime);
        return counter.incrementAndGet();
    }

    public static long generate(String key, Long timeout) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        counter.expire(timeout,TimeUnit.SECONDS);
        return counter.incrementAndGet();
    }

    /**
     * 原子自增
     *
     * @param key
     * @param increment
     * @return
     */
    public static long generate(String key, int increment) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.addAndGet(increment);
    }

    /**
     * 原子自增
     *
     * @param key
     * @param increment
     * @param expireTime
     * @return
     */
    public static long generate(String key, int increment, Date expireTime) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        counter.expireAt(expireTime);
        return counter.addAndGet(increment);
    }

    /**
     * 序列化对象为JSONString
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.SortMapEntriesByKeys);
    }

    /**
     * 序列化JSON为ObjectBean
     *
     * @return
     */
    public static <T> T parseJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 序列化List对象
     *
     * @param key
     * @param clazz
     * @return
     */
    public static <T> List<T> getList(String key, Class<T> clazz) {
        try {
            Object cache = get(key);
            if (null != cache && StrUtil.isNotBlank(cache.toString())) {
                List<T> cacheList = JSONArray.parseArray(JSON.toJSON(cache).toString(), clazz);
                return cacheList;
            }
        } catch (Exception e) {
            log.error("redis查询异常，e:", e);
            return Collections.EMPTY_LIST;
        }
        return null;
    }

    /**
     * 序列化List对象
     *
     * @param key
     * @param clazz
     * @return
     */
    public static <T> List<T> getTList(String key, Class<T> clazz) {
        try {
            Object cache = get(key);
            if (null != cache && StrUtil.isNotBlank(cache.toString())) {
                List<T> cacheList = JSONArray.parseArray(cache.toString(), clazz);
                return cacheList;
            }
        } catch (Exception e) {
            log.error("redis查询异常，e:", e);
            return Collections.EMPTY_LIST;
        }
        return null;
    }

    /**
     * 将List作为JSONString存入缓存
     *
     * @param key
     * @param list
     */
    public static <T> void setList(String key, List<T> list, long time) {
        if (StrUtil.isBlank(key) || CollectionUtils.isEmpty(list)) {
            return;
        }
        set(key, toJson(list), time);
    }

    /**
     * 删除key
     */
    public static void del(String key) {
        if (StrUtil.isNotBlank(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 获取成员的排名
     *
     * @param key    redis中的key
     * @param member 成员信息
     * @return 返回排名（排名默认从0开始，这里直接加1 使其从 1开始）
     */
    public static Long getZrevRank(String key, Object member) {
        Long rank = redisTemplate.opsForZSet().reverseRank(key, member);
        if (rank != null) {
            rank = rank + 1;
        }
        return rank;
    }

    /**
     * 添加zset数据
     *
     * @param key    redis键
     * @param member 成员名称
     * @param score  分数
     * @return 添加结果
     */
    public static boolean addZsetMember(String key, Object member, double score) {
        return redisTemplate.opsForZSet().add(key, member, score);
    }

    /**
     * 增加成员zset成员分数
     * @param key redis key
     * @param member 成员
     * @param score 分数
     * @return
     */
    public static Double zIncrementBy(String key, Object member, double score) {
        return redisTemplate.opsForZSet().incrementScore(key, member, score);
    }


    /**
     * 获取指定数量的排行榜
     *
     * @param key        redis键
     * @param startIndex 开始名词 从0开始
     * @param endIndex   结束名词
     * @return
     */
    public static Set<Object> getZrevRange(String key, int startIndex, int endIndex) {
        return redisTemplate.opsForZSet().reverseRange(key, startIndex, endIndex);
    }

    /**
     * 获取指定数量包含分数的排行榜
     * @param key        redis键
     * @param startIndex 开始名词 从0开始
     * @param endIndex   结束名词
     * @return
     */
    public static Set<ZSetOperations.TypedTuple<Object>> getZrevRangeWithScore(String key, int startIndex, int endIndex) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, startIndex, endIndex);
    }

    /**
     * 获取指定成员的分数
     *
     * @param key    redis键
     * @param member 成员名称
     * @return
     */
    public static Double getScore(String key, Object member) {
        return redisTemplate.opsForZSet().score(key, member);
    }

    /**
     * 移除指定成员
     *
     * @param key    redis键
     * @param member 成员
     * @return 增加后的分数
     */
    public static Long removeMemberFromZset(String key, Object member) {
        return redisTemplate.opsForZSet().remove(key, member);
    }

    /**
     * 获取成员总数
     *
     * @param key redis键
     * @return 成员数量
     */
    public static Long getMembersCountFromZset(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 根据id 和原始分数计算出带小数点的分数
     *
     * @param id
     * @param originScore
     * @return
     */
    public static Double getScoreWithPoint(Long id, String originScore) {
        String pointStr = String.valueOf(Long.MAX_VALUE - id);
        String point = pointStr.substring(pointStr.length() - 5);
        return Double.valueOf(originScore + "." + point);
    }

    @Resource
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {

        if (null == redisTemplate) {
            log.info("Redis初始化配置失败，请检查配置项");
        } else {
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            log.info("Redis初始化配置注入成功！");
        }
        RedisUtils.redisTemplate = redisTemplate;
    }


    public static RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    public void setStringRedisTemplate(RedisTemplate<String, String> stringRedisTemplate) {

        if (null == stringRedisTemplate) {
            log.info("StringRedisTemplate初始化配置失败，请检查配置项");
        } else {
            stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
            stringRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            log.info("StringRedisTemplate初始化配置注入成功！");
        }
        RedisUtils.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取String类型的set 集合
     *
     * @param key
     * @return
     */
    public static Set<String> getSetStringMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 加锁，无阻塞
     *
     * @param key
     * @param expireTime
     * @return
     */
    public static Boolean lock(String key, long expireTime) {
        String requestId = UUID.randomUUID().toString();
        //Set命令返回OK，则证明获取锁成功
        return redisTemplate.opsForValue().setIfAbsent(key, requestId, expireTime,
                TimeUnit.SECONDS);
    }

    /**
     * 根据key获取自增的数
     *
     * @param key redis中的key
     * @return long类型的数字
     */
    public static long getAtomicLong(String key) {
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return redisAtomicLong.getAndIncrement();
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @SuppressWarnings({"unchecked", "static-access"})
    public static <T> T get(String key, Class<T> clazz) {
//        key = setPrefix(key);
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            String temp;
            try {
                temp = new String();

                temp = temp.valueOf(value);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }

            if (clazz.isPrimitive()) {

                if (String.class.isAssignableFrom(clazz)) {
                    return (T) temp;
                } else if (int.class.isAssignableFrom(clazz)) {
                    return (T) new Integer(temp);
                } else if (long.class.isAssignableFrom(clazz)) {
                    return (T) new Long(temp);
                } else if (boolean.class.isAssignableFrom(clazz)) {
                    return (T) new Boolean(temp);
                } else if (double.class.isAssignableFrom(clazz)) {
                    return (T) new Double(temp);
                } else if (float.class.isAssignableFrom(clazz)) {
                    return (T) new Float(temp);
                } else if (byte.class.isAssignableFrom(clazz)) {
                    return (T) new Byte(temp);
                } else if (short.class.isAssignableFrom(clazz)) {
                    return (T) new Short(temp);
                } else {
                    throw new RuntimeException("unsupport type:type=" + clazz.getName() + "value="
                            + temp);
                }
            } else {
                return JSON.parseObject(temp, clazz);
            }
        }

        return null;
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static Set<ZSetOperations.TypedTuple<Object>> zrangeWithScores(String redisKey, long start, long end){
        return redisTemplate.opsForZSet().rangeWithScores(redisKey,start,end);
    }

    public static Set<ZSetOperations.TypedTuple<Object>> zrevrangeWithScores(String redisKey, long start, long end){
        return redisTemplate.opsForZSet().reverseRangeWithScores(redisKey,start,end);
    } /**
     * 单笔执行lua脚本
     * @param luaName
     * @param keys
     * @param args
     * @return
     */
    public static String eval (String luaName,List<String> keys,String... args){
        DefaultRedisScript<String> defaultRedisScript = new DefaultRedisScript<>();
        //defaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("resources"+ File.separator+luaName)));
        defaultRedisScript.setScriptText(luaName);
        defaultRedisScript.setResultType(String.class);
        String results = redisTemplate.execute(defaultRedisScript,keys,args);
        return results;
    }



    public static Map<String, String> mget(List<String> redisKeys) {
        Map<String,String> resultMap = new HashMap<>();
        for (String redisKey : redisKeys) {
            resultMap.put(redisKey,get(redisKey,String.class));
        }
        return resultMap;
    }

}
